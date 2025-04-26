package test.samir.be.app.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import test.samir.be.app.auth.model.LoginDTO;
import test.samir.be.app.auth.model.RefreshTokenDTO;
import test.samir.be.app.auth.model.TokenDTO;
import test.samir.be.app.common.response.WebResponse;
import test.samir.be.app.user.model.CreateUserDTO;
import test.samir.be.app.user.model.UserResponse;

@RestController
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping(
            path = "/auth/register"
    )
    public WebResponse<UserResponse> register(@RequestBody CreateUserDTO dto) {
        UserResponse response = authService.register(dto);

        return WebResponse.<UserResponse>builder().data(response).build();
    }

    @PostMapping("/auth/login")
    public WebResponse<TokenDTO> login(@RequestBody LoginDTO dto) {
        TokenDTO tokenDTO = authService.login(dto);

        return WebResponse.<TokenDTO>builder().data(tokenDTO).build();
    }

    @PostMapping("/auth/refresh")
    public WebResponse<TokenDTO> refresh(@RequestBody RefreshTokenDTO dto) {
        TokenDTO tokenDTO = authService.refresh(dto.getRefreshToken());

        return WebResponse.<TokenDTO>builder().data(tokenDTO).build();
    }


}
