package mezyk.mateusz.app.tasks.core.data.service;

import mezyk.mateusz.app.tasks.core.data.exception.InvalidTaskRequestException;
import mezyk.mateusz.app.tasks.core.data.exception.TaskDataViolationException;
import mezyk.mateusz.app.tasks.core.data.model.Task;
import mezyk.mateusz.app.tasks.core.data.repository.TaskRepository;
import mezyk.mateusz.app.tasks.core.data.exception.TaskNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    public static final String NO_ID_EXCEPTION_MESSAGE = "Task to update has no id";
    public static final String NO_TASK_EXCEPTION_MESSAGE = "There is no task with id = ";
    public static final String NULL_ID = "Can't find task with id == null";

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Task task, Long id) {
        if (id == null || task.getId() == null) {
            throw new TaskNotFoundException(NO_ID_EXCEPTION_MESSAGE);
        }
        if (!id.equals(task.getId())) {
            throw new TaskDataViolationException(
                    String.format("The id of resource object can't be changed. Path variable id = %d while resource object id = %d",
                            id, task.getId()));
        }
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(NO_TASK_EXCEPTION_MESSAGE + task.getId());
        }

        return saveTask(task);
    }

    @Override
    public Task markTaskCompleted(Long id) {
        if (id == null) {
            throw new InvalidTaskRequestException(NULL_ID);
        }
        Task task = findTaskById(id);
        task.setCompleted(true);
        return saveTask(task);
    }

    @Override
    public Task markTaskActive(Long id) {
        if (id == null) {
            throw new InvalidTaskRequestException(NULL_ID);
        }
        Task task = findTaskById(id);
        task.setCompleted(false);
        return saveTask(task);
    }

    @Override
    public Task findTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("There is no task with id = " + id));
    }

    @Override
    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> findAllActiveTasks() {
        return taskRepository.findAllByCompletedFalse();
    }

    @Override
    public List<Task> findAllCompletedTasks() {
        return taskRepository.findAllByCompletedTrue();
    }

    @Override
    public void deleteTaskById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(NO_TASK_EXCEPTION_MESSAGE + id);
        }
        taskRepository.deleteById(id);
    }
}
