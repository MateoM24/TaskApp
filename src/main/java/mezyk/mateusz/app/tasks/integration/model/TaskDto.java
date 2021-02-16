package mezyk.mateusz.app.tasks.integration.model;

public class TaskDto extends CreateTaskDto {

    private Long id;

    private boolean isCompleted;

    public TaskDto() {
    }

    public TaskDto(String title, String description, boolean isCompleted, Long id) {
        super(title, description);
        this.id = id;
        this.isCompleted = isCompleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }
}
