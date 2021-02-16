package mezyk.mateusz.app.tasks;

import mezyk.mateusz.app.tasks.core.data.model.Task;
import mezyk.mateusz.app.tasks.core.data.repository.TaskRepository;
import mezyk.mateusz.app.tasks.integration.model.CreateTaskDto;
import mezyk.mateusz.app.tasks.integration.model.TaskDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EndToEndTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    public void clean() {
        taskRepository.deleteAll();
    }

    @Test
    public void shouldReturnAllTasks() {
        //having
        Task task = createAndSaveTask();
        //when
        TaskDto[] tasks = restTemplate.getForObject("/task", TaskDto[].class);
        //then
        assertEquals(1, tasks.length);
        TaskDto returnedTask = tasks[0];
        assertEquals(task.getTitle(), returnedTask.getTitle());
        assertEquals(task.getDescription(), returnedTask.getDescription());
    }

    @Test
    public void shouldReturnAllActiveTasks() {
        //having
        Task activeTask = new Task("Title A", "Description A");
        taskRepository.save(activeTask);

        Task completedTask = new Task("Title B", "Description B");
        completedTask.setCompleted(true);
        taskRepository.save(completedTask);
        //when
        TaskDto[] tasks = restTemplate.getForObject("/task/active", TaskDto[].class);
        //then
        assertEquals(1, tasks.length);
        TaskDto returnedTask = tasks[0];
        assertEquals(activeTask.getTitle(), returnedTask.getTitle());
        assertEquals(activeTask.getDescription(), returnedTask.getDescription());
        assertFalse(returnedTask.getCompleted());
    }

    @Test
    public void shouldReturnAllCompletedTasks() {
        //having
        Task activeTask = new Task("Title A", "Description A");
        taskRepository.save(activeTask);

        Task completedTask = new Task("Title B", "Description B");
        completedTask.setCompleted(true);
        taskRepository.save(completedTask);
        //when
        TaskDto[] tasks = restTemplate.getForObject("/task/completed", TaskDto[].class);
        //then
        assertEquals(1, tasks.length);
        TaskDto returnedTask = tasks[0];
        assertEquals(completedTask.getTitle(), returnedTask.getTitle());
        assertEquals(completedTask.getDescription(), returnedTask.getDescription());
        assertTrue(returnedTask.getCompleted());
    }

    @Test
    public void shouldCreateTask() {
        //having
        String title = "Title";
        String description = "Description";
        CreateTaskDto dto = new CreateTaskDto(title, description);
        //when
        TaskDto returnedTaskDto = restTemplate.postForObject("/task/new", dto, TaskDto.class);
        //then
        assertNotNull(returnedTaskDto);
        assertEquals(title, returnedTaskDto.getTitle());
        assertEquals(description, returnedTaskDto.getDescription());
        assertNotNull(returnedTaskDto.getId());
        assertFalse(returnedTaskDto.getCompleted());

        taskRepository.existsById(returnedTaskDto.getId());
    }

    @Test
    public void shouldCreateTaskWithTitleAndNullDescription() {
        //having
        String title = "Title";
        CreateTaskDto dto = new CreateTaskDto(title, null);
        //when
        TaskDto savedTask = restTemplate.postForObject("/task/new", dto, TaskDto.class);
        //then
        assertNotNull(savedTask);
        assertEquals(title, savedTask.getTitle());
        assertNull(savedTask.getDescription());
        assertNotNull(savedTask.getId());
        assertFalse(savedTask.getCompleted());
    }

    @Test
    public void shouldNotCreateTaskWithNullTitle() {
        //having
        CreateTaskDto dto = new CreateTaskDto(null, "Description");
        //when
        ResponseEntity<TaskDto> response = restTemplate.postForEntity("/task/new", dto, TaskDto.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, taskRepository.findAll().size());
    }

    @Test
    public void shouldNotCreateTaskWithEmptyTitle() {
        //having
        CreateTaskDto dto = new CreateTaskDto("", "Description");
        //when
        ResponseEntity<TaskDto> response = restTemplate.postForEntity("/task/new", dto, TaskDto.class);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, taskRepository.findAll().size());
    }

    @Test
    public void shouldUpdateTask() {
        //having
        Task originalTask = createAndSaveTask();
        assertFalse(originalTask.getCompleted());
        assertNotNull(originalTask.getId());
        //when
        String newTitle = "Título";
        String newDescription = "Descripción";
        TaskDto dto = new TaskDto(newTitle, newDescription, true, originalTask.getId());
        restTemplate.put("/task/update/" + originalTask.getId(), dto);
        //then
        Optional<Task> optionalSavedTask = taskRepository.findById(originalTask.getId());
        assertTrue(optionalSavedTask.isPresent());
        Task updatedTask = optionalSavedTask.get();
        assertTrue(updatedTask.getCompleted());
        assertEquals(originalTask.getId(), updatedTask.getId());
        assertEquals(newTitle, updatedTask.getTitle());
        assertEquals(newDescription, updatedTask.getDescription());
    }

    @Test
    public void shouldNotUpdateTaskBecauseTaskDoesNotExist() {
        //having
        String title = "Title";
        String description = "Description";
        Long id = 1L;

        TaskDto task = new TaskDto(title, description, false, id);
        //when
        restTemplate.put("/task/update/{id}", task, task.getId());
        //then
        assertFalse(taskRepository.existsById(id));
    }

    @Test
    public void shouldNotUpdateTaskBecauseOfLackOfPathVariableId() {
        //having
        Task task = createAndSaveTask();
        Task savedTask = taskRepository.save(task);
        assertNotNull(savedTask);

        TaskDto dto = new TaskDto("Title B", "Description B", true, savedTask.getId());
        //when
        restTemplate.put("/task/update/", dto);
        //then
        Task retrievedTask = taskRepository.findById(dto.getId()).get();
        assertNotEquals(retrievedTask.getTitle(), dto.getTitle());
        assertNotEquals(retrievedTask.getDescription(), dto.getDescription());
        assertNotEquals(retrievedTask.getCompleted(), dto.getCompleted());
        assertEquals(retrievedTask.getId(), dto.getId());
    }

    @Test
    public void shouldNotUpdateTaskBecauseOfLackOfTaskId() {
        //having
        Task savedTask = createAndSaveTask();
        assertNotNull(savedTask);

        TaskDto dto = new TaskDto("Title B", "Secribtion B", true, null);
        //when
        restTemplate.put("/task/update/{id}", dto, savedTask.getId());
        //then
        Task retrievedTask = taskRepository.findById(savedTask.getId()).get();
        assertNotEquals(retrievedTask.getTitle(), dto.getTitle());
        assertNotEquals(retrievedTask.getDescription(), dto.getDescription());
        assertNotEquals(retrievedTask.getCompleted(), dto.getCompleted());
    }

    @Test
    public void shouldNotUpdateTaskBecauseTaskIdAndPathVariableIdAreDifferent() {
        //having
        Task savedTask = createAndSaveTask();
        assertNotNull(savedTask);

        TaskDto dto = new TaskDto("Title B", "Secribtion B", true, savedTask.getId() + 50);
        assertNotEquals(savedTask.getId(), dto.getId());
        //when
        restTemplate.put("/task/update/{id}", dto, savedTask.getId());
        //then
        Task retrievedTask = taskRepository.findById(savedTask.getId()).get();
        assertNotEquals(retrievedTask.getTitle(), dto.getTitle());
        assertNotEquals(retrievedTask.getDescription(), dto.getDescription());
        assertNotEquals(retrievedTask.getCompleted(), dto.getCompleted());
    }

    @Test
    public void shouldNotUpdateTaskBecauseTaskDtoIsNotValidEmptyTile() {
        //having
        Task savedTask = createAndSaveTask();
        assertNotNull(savedTask);

        TaskDto dto = new TaskDto("", "Secribtion B", true, savedTask.getId() + 50);
        //when
        restTemplate.put("/task/update/{id}", dto, savedTask.getId());
        //then
        Task retrievedTask = taskRepository.findById(savedTask.getId()).get();
        assertNotEquals(retrievedTask.getTitle(), dto.getTitle());
        assertNotEquals(retrievedTask.getDescription(), dto.getDescription());
        assertNotEquals(retrievedTask.getCompleted(), dto.getCompleted());
    }

    @Test
    public void shouldNotUpdateTaskBecauseTaskDtoIsNotValidNullTitle() {
        //having
        Task savedTask = createAndSaveTask();
        assertNotNull(savedTask);

        TaskDto dto = new TaskDto(null, "Secribtion B", true, savedTask.getId() + 50);
        //when
        restTemplate.put("/task/update/{id}", dto, savedTask.getId());
        //then
        Task retrievedTask = taskRepository.findById(savedTask.getId()).get();
        assertNotEquals(retrievedTask.getTitle(), dto.getTitle());
        assertNotEquals(retrievedTask.getDescription(), dto.getDescription());
        assertNotEquals(retrievedTask.getCompleted(), dto.getCompleted());
    }

    @Test
    public void shouldDeleteTask() {
        //having
        Task savedTask = createAndSaveTask();
        //when
        restTemplate.delete("/task/delete/{id}", savedTask.getId());
        //then
        assertEquals(0, taskRepository.findAll().size());
    }

    @Test
    public void shouldNotDeleteTaskBecauseItDoesNotExist() {
        //having
        Task savedTask = createAndSaveTask();
        //when
        restTemplate.delete("/task/delete/{id}", savedTask.getId() + 50);
        //then
        assertEquals(1, taskRepository.findAll().size());
    }

    @Test
    public void shouldNotDeleteTaskBecauseIdIsNotProvided() {
        //having
        Task savedTask = createAndSaveTask();
        //when
        restTemplate.delete("/task/delete/");
        //then
        assertEquals(1, taskRepository.findAll().size());
    }

    @Test
    public void shouldMarkTaskCompleted() {
        //having
        Task task = createAndSaveTask();
        assertFalse(task.getCompleted());
        //when
        TaskDto dto = restTemplate.patchForObject("/task/update/{id}/completed", null, TaskDto.class, task.getId());
        //then
        assertTrue(dto.getCompleted());

        Optional<Task> savedTaskOptional = taskRepository.findById(task.getId());
        assertTrue(savedTaskOptional.isPresent());
        assertTrue(savedTaskOptional.get().getCompleted());
    }

    @Test
    public void shouldNotMakeExistingTaskCompletedWhenProvidingInvalidId() {
        //having
        Task task = createAndSaveTask();
        assertFalse(task.getCompleted());
        //when
        restTemplate.patchForObject("/task/update/{id}/completed", null, TaskDto.class, task.getId() + 1);
        //then
        Task retrievedTask = taskRepository.findById(task.getId()).get();
        assertFalse(retrievedTask.getCompleted());
    }

    @Test
    public void shouldMarkTaskActive() {
        //having
        Task task = new Task("Title", "Description");
        task.setCompleted(true);
        task = taskRepository.save(task);
        assertTrue(task.getCompleted());
        //when
        TaskDto dto = restTemplate.patchForObject("/task/update/{id}/active", null, TaskDto.class, task.getId());
        //then
        assertFalse(dto.getCompleted());

        Optional<Task> savedTaskOptional = taskRepository.findById(task.getId());
        assertTrue(savedTaskOptional.isPresent());
        assertFalse(savedTaskOptional.get().getCompleted());
    }

    @Test
    public void shouldNotMakeExistingTaskActiveWhenProvidingInvalidId() {
        //having
        Task task = new Task("Title", "Description");
        task.setCompleted(true);
        task = taskRepository.save(task);
        assertTrue(task.getCompleted());
        //when
        restTemplate.patchForObject("/task/update/{id}/active", null, TaskDto.class, task.getId() + 1);
        //then
        Task retrievedTask = taskRepository.findById(task.getId()).get();
        assertTrue(retrievedTask.getCompleted());
    }

    private Task createAndSaveTask() {
        return taskRepository.save(new Task("Title", "Description"));
    }

}
