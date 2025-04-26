package test.samir.be.app.circuit_breaker;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/target")
public class TargetApiController {

    @GetMapping
    public String targetApi(
            @RequestParam(defaultValue = "true") boolean success
    )  {
        if (success) {
            return "Success Response from Target API!";
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Simulated failure from Target API!");
        }
    }
}
