package test.samir.be.app.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import test.samir.be.app.common.validation.ValidationService;
import test.samir.be.app.user.User;
import test.samir.be.app.user.UserRepository;
import test.samir.be.app.user.UserService;
import test.samir.be.app.user.model.CreateUserDTO;
import test.samir.be.app.user.model.UserResponse;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ValidationService validationService;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserResponse registerUser(CreateUserDTO dto) {
        validationService.validate(dto);

        Boolean existsByUsername = userRepository.existsByUsername(dto.getUsername());

        if (existsByUsername) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);

        return toUserResponse(user);
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}
