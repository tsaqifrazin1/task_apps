package test.samir.be.app.circuit_breaker;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiClientService {

    private final RestTemplate restTemplate;
    private final String targetApiUrl;

    public ApiClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.targetApiUrl = "http://localhost:8080/api/target";
    }

    @CircuitBreaker(name = "targetApiCircuitBreaker", fallbackMethod = "fallbackTargetApi")
    public String callTargetApi(boolean success) {
        String url = targetApiUrl + "?success=" + success;
        return restTemplate.getForObject(url, String.class);
    }

    public String fallbackTargetApi(boolean success, Throwable throwable) {
        return "Fallback response because Target API is unavailable.";
    }
}

