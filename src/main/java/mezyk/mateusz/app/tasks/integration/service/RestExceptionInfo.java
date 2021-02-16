package mezyk.mateusz.app.tasks.integration.service;

import org.springframework.http.HttpStatus;

public class RestExceptionInfo {

    private HttpStatus status;

    private String message;

    public RestExceptionInfo() {
    }

    public RestExceptionInfo(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
