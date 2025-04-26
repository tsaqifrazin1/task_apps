package test.samir.be.app.circuit_breaker;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final ApiClientService apiClientService;

    public TestController(ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }

    @GetMapping
    public String testApi(
            @RequestParam(defaultValue = "true") boolean success
    ) {
        return String.valueOf(apiClientService.callTargetApi(success));
    }
}
