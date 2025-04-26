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
import test.samir.be.app.auth.model.LoginDTO;
import test.samir.be.app.auth.model.TokenDTO;
import test.samir.be.app.common.response.WebResponse;
import test.samir.be.app.tags.TagRepository;
import test.samir.be.app.task.TaskRepository;
import test.samir.be.app.user.User;
import test.samir.be.app.user.UserRepository;
import test.samir.be.app.user.model.CreateUserDTO;
import test.samir.be.app.user.model.UserResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.profiles.active=test")
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp(){
        tagRepository.deleteAll();
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void successRegisterUser() throws Exception{
        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("Username");
        dto.setPassword("rahasia");

        mockMvc.perform(
                post("/auth/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<UserResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>() {
                            }
                    );

                    assertNull(response.getErrors());
                }
        );
    }

    @Test
    void failedRegisterUserExisting() throws Exception{
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("Username");
        user.setPassword(passwordEncoder.encode("rahasia"));

        userRepository.save(user);

        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("Username");
        dto.setPassword("rahasia");

        mockMvc.perform(
                post("/auth/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result -> {
                    WebResponse<UserResponse> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>() {
                            }
                    );

                    assertNotNull(response.getErrors());
                }
        );
    }

    @Test
    void successLogin() throws Exception{
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("Username");
        user.setPassword(passwordEncoder.encode("rahasia"));

        userRepository.save(user);

        LoginDTO dto = new LoginDTO();
        dto.setUsername("Username");
        dto.setPassword("rahasia");

        mockMvc.perform(
                post("/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<TokenDTO> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), new TypeReference<WebResponse<TokenDTO>>() {
                            }
                    );

                    assertNull(response.getErrors());
                }
        );
    }

    @Test
    void failedLoginUsername() throws Exception{
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("Username");
        user.setPassword(passwordEncoder.encode("rahasia"));

        userRepository.save(user);

        LoginDTO dto = new LoginDTO();
        dto.setUsername("Username salah");
        dto.setPassword("rahasia");

        mockMvc.perform(
                post("/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result -> {
                    WebResponse<TokenDTO> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), new TypeReference<WebResponse<TokenDTO>>() {
                            }
                    );

                    assertNotNull(response.getErrors());
                }
        );
    }

    @Test
    void failedLoginPassword() throws Exception{
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("Username");
        user.setPassword(passwordEncoder.encode("rahasia"));

        userRepository.save(user);

        LoginDTO dto = new LoginDTO();
        dto.setUsername("Username");
        dto.setPassword("salah");

        mockMvc.perform(
                post("/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result -> {
                    WebResponse<TokenDTO> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), new TypeReference<WebResponse<TokenDTO>>() {
                            }
                    );

                    assertNotNull(response.getErrors());
                }
        );
    }

    @Test
    void successRefreshToken() throws Exception{
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("Username");
        user.setPassword(passwordEncoder.encode("rahasia"));

        userRepository.save(user);

        LoginDTO dto = new LoginDTO();
        dto.setUsername("Username");
        dto.setPassword("rahasia");

        AtomicReference<String> refreshToken = new AtomicReference<>();
        mockMvc.perform(
                post("/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<TokenDTO> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), new TypeReference<WebResponse<TokenDTO>>() {
                            }
                    );

                    assertNull(response.getErrors());

                    refreshToken.set(response.getData().getRefreshToken());
                }
        );
        Map<String, Object> payload = new HashMap<>();
        payload.put("refreshToken", refreshToken); // hanya satu field

        mockMvc.perform(
                post("/auth/refresh")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<TokenDTO> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), new TypeReference<WebResponse<TokenDTO>>() {
                            }
                    );

                    assertNull(response.getErrors());
                }
        );
    }

    @Test
    void failedRefreshToken() throws Exception{
        Map<String, Object> payload = new HashMap<>();
        payload.put("refreshToken", "invalid refresh token"); // hanya satu field

        mockMvc.perform(
                post("/auth/refresh")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result -> {
                    WebResponse<TokenDTO> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(), new TypeReference<WebResponse<TokenDTO>>() {
                            }
                    );

                    assertNotNull(response.getErrors());
                }
        );
    }
}
