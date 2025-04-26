package test.samir.be.app.task.impl;

import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import test.samir.be.app.common.validation.ValidationService;
import test.samir.be.app.tags.Tag;
import test.samir.be.app.tags.TagRepository;
import test.samir.be.app.tags.model.TagResponse;
import test.samir.be.app.task.Task;
import test.samir.be.app.task.TaskRepository;
import test.samir.be.app.task.TaskService;
import test.samir.be.app.task.model.CreateTaskDTO;
import test.samir.be.app.task.model.FilterTaskDTO;
import test.samir.be.app.task.model.TaskResponse;
import test.samir.be.app.user.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    ValidationService validationService;

    @Transactional
    public TaskResponse createTask(CreateTaskDTO dto, User user) {
        validationService.validate(dto);

        Set<Tag> tags = dto.getTags().stream().map(tagName -> {
            Optional<Tag> optionalTag = tagRepository.findFirstByNameAndUser(tagName, user);
            return optionalTag.orElseGet(() -> {
                Tag newTag = new Tag();
                newTag.setId(UUID.randomUUID().toString());
                newTag.setName(tagName);
                newTag.setUser(user);

                tagRepository.save(newTag);
                return newTag;
            });
        }).collect(Collectors.toSet());

        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setTitle(dto.getTitle());
        task.setNotes(dto.getNotes());
        task.setDueDate(dto.getDueDate());
        task.setPriority(dto.getPriority());
        task.setTags(tags);
        task.setUser(user);

        taskRepository.save(task);


        return toTaskResponse(task);
    }

    @Transactional(readOnly = true)
    public TaskResponse findTaskById(String id, User user) {
        Task task = taskRepository.findFirstByIdAndUser(id, user)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task Not Found")
                );
        return toTaskResponse(task);
    }

    @Transactional
    public TaskResponse updateTaskCompleted(String id, User user){
        Task task = taskRepository.findFirstByIdAndUser(id, user)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task Not Found")
                );

        if(task.getCompleted()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task is already complete");

        task.setCompleted(true);
        task.setCompletedAt(new Date().toInstant());
        taskRepository.save(task);

        return toTaskResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findTasks(FilterTaskDTO filter, User user){
        validationService.validate(filter);
        Specification<Task> specification = ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("user"), user));
            if(Objects.nonNull(filter.getCompleted())){
                predicates.add(criteriaBuilder.equal(root.get("completed"), filter.getCompleted()));
            }
            if(Objects.nonNull(filter.getDueBefore())){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dueDate"), filter.getDueBefore()));
            }
            if(Objects.nonNull(filter.getPriority())){
                predicates.add(criteriaBuilder.equal(root.get("priority"), filter.getPriority()));
            }
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        });

        Sort sort = Sort.unsorted();
        if (Objects.nonNull(filter.getSortBy())) {
            sort = Sort.by(Sort.Direction.DESC, filter.getSortBy().toString());
        }

        List<Task> tasks = taskRepository.findAll(specification, sort);

        return tasks.stream().map(this::toTaskResponse).collect(Collectors.toList());
    }

    private TaskResponse toTaskResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .notes(task.getNotes())
                .dueDate(task.getDueDate())
                .priority(task.getPriority())
                .completed(task.getCompleted())
                .completedAt(task.getCompletedAt())
                .createdAt(task.getCreatedAt())
                .tags(task.getTags().stream()
                        .map(tag -> new TagResponse(tag.getId(), tag.getName()))
                        .collect(Collectors.toSet())
                )
                .build();
    }
}
