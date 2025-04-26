package test.samir.be.app.auth.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import test.samir.be.app.auth.AuthService;
import test.samir.be.app.auth.model.LoginDTO;
import test.samir.be.app.auth.model.TokenDTO;
import test.samir.be.app.common.utils.JwtUtils;
import test.samir.be.app.common.validation.ValidationService;
import test.samir.be.app.user.User;
import test.samir.be.app.user.UserService;
import test.samir.be.app.user.model.CreateUserDTO;
import test.samir.be.app.user.model.UserResponse;

import java.util.Objects;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    ValidationService validationService;

    @Transactional
    public UserResponse register(CreateUserDTO dto) {
        return userService.registerUser(dto);
    }

    @Transactional(readOnly = true)
    public TokenDTO login(LoginDTO dto) {
        validationService.validate(dto);

        User user = userService.findUserByUsername(dto.getUsername());

        if(Objects.isNull(user)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password or username");
        }
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password or username");
        }

        TokenDTO token = new TokenDTO();
        token.setAccessToken(jwtUtils.generateAccessToken(user.getUsername()));
        token.setRefreshToken(jwtUtils.generateRefreshToken(user.getUsername()));

        return token;
    }

    public TokenDTO refresh(String refreshToken) {
        if (!jwtUtils.isTokenValid(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Refresh Token");
        }

        String username = jwtUtils.extractUsername(refreshToken);
        TokenDTO token = new TokenDTO();
        token.setAccessToken(jwtUtils.generateAccessToken(username));
        token.setRefreshToken(jwtUtils.generateRefreshToken(username));

        return token;
    }
}
