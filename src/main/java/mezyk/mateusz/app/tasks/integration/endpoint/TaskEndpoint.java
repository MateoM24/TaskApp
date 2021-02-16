package mezyk.mateusz.app.tasks.integration.endpoint;

import mezyk.mateusz.app.tasks.core.data.model.Task;
import mezyk.mateusz.app.tasks.core.data.service.TaskService;
import mezyk.mateusz.app.tasks.integration.model.CreateTaskDto;
import mezyk.mateusz.app.tasks.integration.model.TaskDto;
import mezyk.mateusz.app.tasks.integration.service.TaskMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("task")
public class TaskEndpoint {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TaskEndpoint(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @GetMapping()
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<Task> tasks = taskService.findAllTasks();
        return new ResponseEntity<>(taskMapper.mapToDtos(tasks), HttpStatus.OK);
    }

    @GetMapping("/active")
    public ResponseEntity<List<TaskDto>> getAllActiveTasks() {
        List<Task> tasks = taskService.findAllActiveTasks();
        return new ResponseEntity<>(taskMapper.mapToDtos(tasks), HttpStatus.OK);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<TaskDto>> getAllCompletedTasks() {
        List<Task> tasks = taskService.findAllCompletedTasks();
        return new ResponseEntity<>(taskMapper.mapToDtos(tasks), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@NotNull @PathVariable Long id) {
        return new ResponseEntity<>(taskMapper.mapToDto(taskService.findTaskById(id)), HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody CreateTaskDto createTaskDto) {
        Task task = taskService.saveTask(taskMapper.mapToEntity(createTaskDto));
        return new ResponseEntity<>(taskMapper.mapToDto(task), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TaskDto> updateTask(@Valid @RequestBody TaskDto taskDto, @NotNull @PathVariable Long id) {
        Task task = taskService.updateTask(taskMapper.mapToEntity(taskDto), id);
        return new ResponseEntity<>(taskMapper.mapToDto(task), HttpStatus.OK);
    }

    @PatchMapping("/update/{id}/active")
    public ResponseEntity<TaskDto> setTaskActive(@NotNull @PathVariable Long id) {
        Task task = taskService.markTaskActive(id);
        return new ResponseEntity<>(taskMapper.mapToDto(task), HttpStatus.OK);
    }

    @PatchMapping("/update/{id}/completed")
    public ResponseEntity<TaskDto> setTaskCompleted(@NotNull @PathVariable Long id) {
        Task task = taskService.markTaskCompleted(id);
        return new ResponseEntity<>(taskMapper.mapToDto(task), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTask(@NotNull @PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.ok().build();
    }



}
