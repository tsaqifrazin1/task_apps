package test.samir.be.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import test.samir.be.app.common.constant.PriorityEnum;
import test.samir.be.app.common.response.WebResponse;
import test.samir.be.app.common.utils.JwtUtils;
import test.samir.be.app.tags.Tag;
import test.samir.be.app.tags.TagRepository;
import test.samir.be.app.task.Task;
import test.samir.be.app.task.TaskRepository;
import test.samir.be.app.task.model.CreateTaskDTO;
import test.samir.be.app.task.model.FilterTaskDTO;
import test.samir.be.app.task.model.TaskResponse;
import test.samir.be.app.user.User;
import test.samir.be.app.user.UserRepository;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
public class TaskControllerTest {

    private String accessToken;

    private User user;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.findByUsername("Username");  // replace with the username you are using
        if (Objects.isNull(user)) {
            user = new User();
            user.setId(UUID.randomUUID().toString());
            user.setUsername("Username");
            user.setPassword(passwordEncoder.encode("rahasia"));
            userRepository.save(user);
        }

        userRepository.save(user);

        accessToken = jwtUtils.generateAccessToken(user.getUsername());
    }

    @Test
    @Transactional
    void successCreateTask() throws Exception {
        CreateTaskDTO dto = new CreateTaskDTO();
        dto.setTitle("Finish THT");
        dto.setNotes("Samir Take Home Test");
        dto.setDueDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
        dto.setPriority(PriorityEnum.LOW);
        dto.setTags(Arrays.asList("Tags1", "Tags2"));

        mockMvc.perform(
                post("/api/tasks")
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<TaskResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<WebResponse<TaskResponse>>() {
                    }
            );
            assertNull(response.getErrors());

            Task taskDb = taskRepository.findFirstByIdAndUser(response.getData().getId(), user).orElseThrow();

            assertEquals("Finish THT", taskDb.getTitle());
            assertEquals("Samir Take Home Test", taskDb.getNotes());
            assertEquals(PriorityEnum.LOW, taskDb.getPriority());
            assertTrue(
                    taskDb.getTags().stream()
                            .map(Tag::getName)
                            .collect(Collectors.toSet())
                            .containsAll(Set.of("Tags1", "Tags2"))
            );
            assertEquals(false, taskDb.getCompleted());
        });
    }

    @Test
    void failedCreateTaskWithNullTitle() throws Exception {
        CreateTaskDTO dto = new CreateTaskDTO();
        dto.setNotes("Samir Take Home Test");
        dto.setDueDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
        dto.setPriority(PriorityEnum.LOW);

        mockMvc.perform(
                post("/api/tasks")
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                            }
                    );

                    assertNotNull(response.getErrors());
                }
        );
    }

    @Test
    void successFindTaskById() throws Exception {
        Tag tag1 = new Tag();
        tag1.setId(UUID.randomUUID().toString());
        tag1.setName("Tag 1");
        tag1.setUser(user);

        Tag tag2 = new Tag();
        tag2.setId(UUID.randomUUID().toString());
        tag2.setName("Tag 2");
        tag2.setUser(user);

        List<Tag> tags = Arrays.asList(tag1, tag2);
        tagRepository.saveAll(tags);

        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setTitle("Test");
        task.setNotes("test");
        task.setDueDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
        task.setPriority(PriorityEnum.LOW);
        task.setTags(new HashSet<>(Arrays.asList(tag1, tag2)));
        task.setUser(user);

        taskRepository.save(task);


        mockMvc.perform(
                get("/api/tasks/" + task.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<TaskResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), new TypeReference<WebResponse<TaskResponse>>() {
                            }
                    );
                    assertNull(response.getErrors());
                }
        );
    }

    @Test
    void failedFindTaskById() throws Exception {
        mockMvc.perform(
                get("/api/tasks/" + UUID.randomUUID().toString())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(
                result -> {
                    WebResponse<TaskResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), new TypeReference<WebResponse<TaskResponse>>() {
                            }
                    );
                    assertNotNull(response.getErrors());
                }
        );
    }

    @Test
    void successUpdateTaskByIdComplete() throws Exception {
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setTitle("Test");
        task.setNotes("test");
        task.setDueDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
        task.setPriority(PriorityEnum.LOW);
        task.setUser(user);

        taskRepository.save(task);


        mockMvc.perform(
                patch("/api/tasks/" + task.getId() + "/complete")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<TaskResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), new TypeReference<WebResponse<TaskResponse>>() {
                            }
                    );
                    assertNull(response.getErrors());

                    Task taskDb = taskRepository.findFirstByIdAndUser(response.getData().getId(), user).orElseThrow();

                    assertEquals(true, taskDb.getCompleted());
                }
        );
    }

    @Test
    void failedUpdateTaskByIdComplete() throws Exception {
        mockMvc.perform(
                patch("/api/tasks/" + UUID.randomUUID().toString() + "/complete")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(
                result -> {
                    WebResponse<TaskResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), new TypeReference<WebResponse<TaskResponse>>() {
                            }
                    );
                    assertNotNull(response.getErrors());
                }
        );
    }

    @Test
    void successFindTasks() throws Exception {

        Task task1 = new Task();
        task1.setId(UUID.randomUUID().toString());
        task1.setTitle("Test");
        task1.setNotes("test");
        task1.setDueDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
        task1.setPriority(PriorityEnum.LOW);
        task1.setUser(user);

        Task task2 = new Task();
        task2.setId(UUID.randomUUID().toString());
        task2.setTitle("Test2");
        task2.setNotes("test2");
        task2.setCompleted(true);
        task2.setDueDate(Date.from(Instant.now().minus(1, ChronoUnit.DAYS)));
        task2.setPriority(PriorityEnum.HIGH);
        task2.setUser(user);

        Task task3 = new Task();
        task3.setId(UUID.randomUUID().toString());
        task3.setTitle("Test3");
        task3.setNotes("test3");
        task3.setCompleted(true);
        task3.setDueDate(Date.from(Instant.now().minus(2, ChronoUnit.DAYS)));
        task3.setPriority(PriorityEnum.HIGH);
        task3.setUser(user);


        List<Task> tasks = Arrays.asList(task1, task2, task3);
        taskRepository.saveAll(tasks);

        FilterTaskDTO filterTaskDTO = new FilterTaskDTO();
        filterTaskDTO.setPriority(PriorityEnum.HIGH);
        filterTaskDTO.setCompleted(true);
        filterTaskDTO.setDueBefore(new Date());

        mockMvc.perform(
                get("/api/tasks")
                        .header("Authorization", "Bearer " + accessToken)
                        .queryParam("priority", filterTaskDTO.getPriority().toString())
                        .queryParam("completed", filterTaskDTO.getCompleted().toString())
                        .queryParam("dueBefore", DateTimeFormatter.ISO_INSTANT.format(filterTaskDTO.getDueBefore().toInstant()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<List<TaskResponse>> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<TaskResponse>>>() {
                            }
                    );
                    assertNull(response.getErrors());

                    assertEquals(2, response.getData().size());
                    assertTrue(response.getData().get(0).isCompleted());
                    assertEquals(PriorityEnum.HIGH, response.getData().get(0).getPriority());
                    assertTrue(new Date().after(response.getData().get(0).getDueDate()));
                }
        );
    }
}
