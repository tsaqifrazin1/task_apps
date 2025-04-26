package test.samir.be.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import test.samir.be.app.common.constant.PriorityEnum;
import test.samir.be.app.common.response.WebResponse;
import test.samir.be.app.tags.Tag;
import test.samir.be.app.tags.TagRepository;
import test.samir.be.app.tags.model.FilterTagDTO;
import test.samir.be.app.tags.model.TagResponse;
import test.samir.be.app.task.TaskRepository;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.profiles.active=test")
public class TagControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        tagRepository.deleteAll();
    }

    @Test
    void successFindTags() throws Exception {
        Tag tag1 = new Tag();
        tag1.setId(UUID.randomUUID().toString());
        tag1.setName("Tag 1");

        Tag tag2 = new Tag();
        tag2.setId(UUID.randomUUID().toString());
        tag2.setName("Tag 2");

        Tag tag3 = new Tag();
        tag3.setId(UUID.randomUUID().toString());
        tag3.setName("Tag 3");

        List<Tag> tags = Arrays.asList(tag1, tag2, tag3);
        tagRepository.saveAll(tags);

        FilterTagDTO filterTagDTO = new FilterTagDTO();
        filterTagDTO.setName("Tag 1");

        mockMvc.perform(
                get("/api/tags")
                        .queryParam("name", filterTagDTO.getName())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<List<TagResponse>> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<TagResponse>>>() {
                            }
                    );
                    assertNull(response.getErrors());

                    assertEquals(1, response.getData().size());
                    assertEquals(filterTagDTO.getName(), response.getData().get(0).getName());

                }
        );
    }
}
