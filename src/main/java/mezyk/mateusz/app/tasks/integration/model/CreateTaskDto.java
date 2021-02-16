package mezyk.mateusz.app.tasks.integration.model;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;

public class CreateTaskDto implements Serializable {

    @NotBlank
    private String title;

    private String description;

    public CreateTaskDto() {
    }

    public CreateTaskDto(String title, String description) {
        this.title = title;
        this.description = description;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
