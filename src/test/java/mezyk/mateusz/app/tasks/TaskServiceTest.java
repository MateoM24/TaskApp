package mezyk.mateusz.app.tasks;

import mezyk.mateusz.app.tasks.core.data.exception.InvalidTaskRequestException;
import mezyk.mateusz.app.tasks.core.data.exception.TaskDataViolationException;
import mezyk.mateusz.app.tasks.core.data.model.Task;
import mezyk.mateusz.app.tasks.core.data.repository.TaskRepository;
import mezyk.mateusz.app.tasks.core.data.exception.TaskNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import mezyk.mateusz.app.tasks.core.data.service.TaskServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TaskServiceTest {

    private static final String TASK_TITLE = "Crea aplicación";
    private static final String TASK_DESCRIPTION =
            "La aplicación debe permitir tanto la creación de tareas nuevas, como el borrado y la edición de tareas existentes.";

    @Autowired
    private TaskServiceImpl taskService;

    @Autowired
    private TaskRepository repository;

    @AfterEach
    public void clean() {
        repository.deleteAll();
    }

    @Test
    public void shouldSaveOneRecord() {
        //having
        Task taskToSave = createTestTask();
        //when
        taskService.saveTask(taskToSave);
        //then
        List<Task> allTasks = repository.findAll();
        assertEquals(1, allTasks.size());

        Task savedTask = allTasks.get(0);
        assertNotNull(savedTask.getId());
        assertEquals(taskToSave.getTitle(), savedTask.getTitle());
        assertEquals(taskToSave.getDescription(), savedTask.getDescription());
        assertFalse(savedTask.getCompleted());
    }

    @Test
    public void shouldSaveMultipleRecords() {
        //having
        String titleOne = "title One";
        String descriptionOne = "description One";
        Task taskToSaveOne = new Task(titleOne, descriptionOne);

        String titleTwo = "title Two";
        String descriptionTwo = "description Two";
        Task taskToSaveTwo = new Task(titleTwo, descriptionTwo);

        String titleThree = "title Three";
        String descriptionThree = "description Three";
        Task taskToSaveThree = new Task(titleThree, descriptionThree);

        //when
        taskService.saveTask(taskToSaveOne);
        taskService.saveTask(taskToSaveTwo);
        taskService.saveTask(taskToSaveThree);

        //then
        List<Task> allTasks = repository.findAll();
        assertEquals(3, allTasks.size());

        assertTrue(allTasks.stream().anyMatch(task -> titleOne.equals(task.getTitle()) && descriptionOne.equals(task.getDescription())));
        assertTrue(allTasks.stream().anyMatch(task -> titleTwo.equals(task.getTitle()) && descriptionTwo.equals(task.getDescription())));
        assertTrue(allTasks.stream().anyMatch(task -> titleThree.equals(task.getTitle()) && descriptionThree.equals(task.getDescription())));
    }

    @Test
    public void shouldUpdateTask() {
        //having
        Task savedTask = repository.save(createTestTask());
        String newTitle ="Entrega el proyecto";
        String newDescription = "Y que corra bien la aplicación.";
        savedTask.setTitle(newTitle);
        savedTask.setDescription(newDescription);
        savedTask.setCompleted(true);
        //when
        Task updatedTask = taskService.updateTask(savedTask, savedTask.getId());
        //then
        assertEquals(savedTask.getId(), updatedTask.getId());
        assertEquals(newTitle, updatedTask.getTitle());
        assertEquals(newDescription, updatedTask.getDescription());
        assertTrue(updatedTask.getCompleted());
    }

    @Test
    public void shouldFindActiveTasks() {
        //having
        Task activeTask = createTestTask();

        Task completedTask = createTestTask();
        completedTask.setCompleted(true);

        repository.save(activeTask);
        repository.save(completedTask);

        //when
        List<Task> allActiveTasks = taskService.findAllActiveTasks();

        //then
        assertEquals(1, allActiveTasks.size());
        Task foundTask = allActiveTasks.get(0);
        assertEquals(activeTask.getId(), foundTask.getId());
        assertFalse(foundTask.getCompleted());
    }

    @Test
    public void shouldFindCompletedTasks() {
        //having
        Task activeTask = createTestTask();
        Task completedTask = createTestTask();
        completedTask.setCompleted(true);

        repository.save(activeTask);
        repository.save(completedTask);

        //when
        List<Task> allCompletedTasks = taskService.findAllCompletedTasks();

        //then
        assertEquals(1, allCompletedTasks.size());
        Task foundTask = allCompletedTasks.get(0);
        assertEquals(completedTask.getId(), foundTask.getId());
        assertTrue(foundTask.getCompleted());
    }

    @Test
    public void shouldDeleteTask() {
        //having
        Task taskToDelete = repository.save(createTestTask());
        Task taskToKeep = repository.save(createTestTask());
        assertEquals(2, repository.findAll().size());
        //when
        taskService.deleteTaskById(taskToDelete.getId());
        //then
        List<Task> allTasks = taskService.findAllTasks();
        assertEquals(1, allTasks.size());
        Task taskFromDb = allTasks.get(0);
        assertEquals(taskToKeep.getId(), taskFromDb.getId());
    }

    @Test
    public void shouldThrowExceptionOnUpdateBecauseOfLackingEntityId() {
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(createTestTask(), 1L));
        assertEquals(TaskServiceImpl.NO_ID_EXCEPTION_MESSAGE, exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionOnUpdateBecauseOfLackingId() {
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> {
            Task task = createTestTask();
            taskService.updateTask(task, null);
        });
        assertTrue(exception.getMessage().contains(TaskServiceImpl.NO_ID_EXCEPTION_MESSAGE));
    }

    @Test
    public void shouldThrowExceptionOnUpdateBecauseOfMismatchingIds() {
        Task task = createTestTask();
        Task savedTask = repository.save(task);
        assertThrows(TaskDataViolationException.class, () -> taskService.updateTask(savedTask, 2L));
    }

    @Test
    public void shouldThrowExceptionOnUpdateBecauseThereIsNoTaskInDb() {
        Task task = createTestTask();
        task.setId(1L);
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(task, task.getId()));
        assertTrue(exception.getMessage().contains(TaskServiceImpl.NO_TASK_EXCEPTION_MESSAGE));
    }

    @Test
    public void shouldThrowExceptionOnTryingToDeleteNotExistingTask() {
        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTaskById(9L));
    }

    @Test
    public void shouldSetTaskCompleted() {
        //having
        Task task = repository.save(createTestTask());
        assertFalse(task.getCompleted());
        //when
        Task updatedTask = taskService.markTaskCompleted(task.getId());
        //then
        assertTrue(updatedTask.getCompleted());
    }

    @Test
    public void shouldFailSettingNotExistingTaskCompleted() {
        Long id = 1L;
        assertFalse(repository.existsById(id));

        assertThrows(TaskNotFoundException.class, () -> taskService.markTaskCompleted(id));
    }

    @Test
    public void shouldFailSettingTaskCompletedProvidingNullId() {
        assertThrows(InvalidTaskRequestException.class, () -> taskService.markTaskCompleted(null));
    }

    @Test
    public void shouldSetTaskActive() {
        //having
        Task task = createTestTask();
        task.setCompleted(true);
        task = repository.save(task);
        assertTrue(task.getCompleted());
        //when
        Task updatedTask = taskService.markTaskActive(task.getId());
        //then
        assertFalse(updatedTask.getCompleted());
    }

    @Test
    public void shouldFailSettingNotExistingTaskActive() {
        Long id = 1L;
        assertFalse(repository.existsById(id));

        assertThrows(TaskNotFoundException.class, () -> taskService.markTaskActive(id));
    }

    @Test
    public void shouldFailSettingTaskActiveProvidingNullId() {
        assertThrows(InvalidTaskRequestException.class, () -> taskService.markTaskActive(null));
    }

    private Task createTestTask() {
        return new Task(TASK_TITLE, TASK_DESCRIPTION);
    }

}
