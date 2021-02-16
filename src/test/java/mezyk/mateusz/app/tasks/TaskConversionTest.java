package mezyk.mateusz.app.tasks;

import mezyk.mateusz.app.tasks.core.data.model.Task;
import mezyk.mateusz.app.tasks.integration.model.CreateTaskDto;
import mezyk.mateusz.app.tasks.integration.model.TaskDto;
import mezyk.mateusz.app.tasks.integration.service.TaskMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TaskConversionTest {

    @Autowired
    private TaskMapper taskMapper;

    @Test
    public void canConvertEntityToTaskDto() {
        //having
        Long id = 1L;
        String title = "title";
        String description = "description";
        boolean isCompleted = true;

        Task entity = new Task(title, description);
        entity.setId(id);
        entity.setCompleted(isCompleted);

        //when
        TaskDto dto = taskMapper.mapToDto(entity);

        //then
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getTitle(), dto.getTitle());
        assertEquals(entity.getDescription(), dto.getDescription());
        assertEquals(entity.getCompleted(), dto.getCompleted());
    }

    @Test
    public void canConvertTaskDtoToEntity() {
        //having
        Long id = 1L;
        String title = "title";
        String description = "description";
        boolean isCompleted = true;

        TaskDto dto = new TaskDto(title, description, isCompleted, id);

        //when
        Task entity = taskMapper.mapToEntity(dto);

        //then
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getTitle(), entity.getTitle());
        assertEquals(dto.getDescription(), entity.getDescription());
        assertEquals(dto.getCompleted(), entity.getCompleted());
    }

    @Test
    public void canConvertCreateTaskDtoToEntity() {
        //having
        String title = "title";
        String description = "description";

        CreateTaskDto dto = new CreateTaskDto(title, description);

        //when
        Task entity = taskMapper.mapToEntity(dto);

        //then
        assertEquals(dto.getTitle(), entity.getTitle());
        assertEquals(dto.getDescription(), entity.getDescription());
        assertNull(entity.getId());
        assertFalse(entity.getCompleted());
    }

}
