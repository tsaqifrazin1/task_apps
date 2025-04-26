package test.samir.be.app.tags.impl;

import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import test.samir.be.app.common.validation.ValidationService;
import test.samir.be.app.tags.Tag;
import test.samir.be.app.tags.TagRepository;
import test.samir.be.app.tags.TagService;
import test.samir.be.app.tags.model.FilterTagDTO;
import test.samir.be.app.tags.model.TagResponse;
import test.samir.be.app.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    TagRepository tagRepository;

    @Autowired
    ValidationService validationService;

    public List<TagResponse> findTags(FilterTagDTO dto) {
        validationService.validate(dto);
        Specification<Tag> specification = ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(Objects.nonNull(dto.getName())){
                predicates.add(criteriaBuilder.equal(root.get("name"), dto.getName()));
            }
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        });

        List<Tag> tags = tagRepository.findAll(specification);
        return tags.stream().map(this::toTagResponse).toList();
    }

    TagResponse toTagResponse(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
}
