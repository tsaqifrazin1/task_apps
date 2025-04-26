package test.samir.be.app.user;

import test.samir.be.app.user.model.CreateUserDTO;
import test.samir.be.app.user.model.UserResponse;

import java.util.Optional;

public interface UserService {
    User findUserByUsername(String username);
    UserResponse registerUser(CreateUserDTO dto);
}
