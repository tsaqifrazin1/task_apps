package test.samir.be.app.auth;

import test.samir.be.app.auth.model.LoginDTO;
import test.samir.be.app.auth.model.TokenDTO;
import test.samir.be.app.user.model.CreateUserDTO;
import test.samir.be.app.user.model.UserResponse;

public interface AuthService {
    UserResponse register(CreateUserDTO dto);
    TokenDTO login(LoginDTO dto);
    TokenDTO refresh(String refreshToken);
}
