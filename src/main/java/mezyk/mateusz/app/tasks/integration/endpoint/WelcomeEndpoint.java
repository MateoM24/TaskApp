package mezyk.mateusz.app.tasks.integration.endpoint;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class WelcomeEndpoint {

    @GetMapping
    public String welcome() {
        return "Welcome to task app.<br>The easiest way to test API is by accessing <a href=\"http://localhost:8080/swagger-ui.html\">http://localhost:8080/swagger-ui.html</a>";
    }
}
