package mezyk.mateusz.app.tasks.core.data.service;

import mezyk.mateusz.app.tasks.core.data.model.Task;

import java.util.List;

public interface TaskService {

    Task saveTask(Task task);

    Task updateTask(Task task, Long id);

    Task markTaskCompleted(Long id);

    Task markTaskActive(Long id);

    Task findTaskById(Long id);

    List<Task> findAllTasks();

    List<Task> findAllActiveTasks();

    List<Task> findAllCompletedTasks();

    void deleteTaskById(Long id);

}
