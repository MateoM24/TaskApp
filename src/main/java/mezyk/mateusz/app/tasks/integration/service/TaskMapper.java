package mezyk.mateusz.app.tasks.integration.service;

import mezyk.mateusz.app.tasks.core.data.model.Task;
import mezyk.mateusz.app.tasks.integration.model.CreateTaskDto;
import mezyk.mateusz.app.tasks.integration.model.TaskDto;

import java.util.List;

public interface TaskMapper {

    Task mapToEntity(CreateTaskDto dto);

    Task mapToEntity(TaskDto dto);

    TaskDto mapToDto(Task entity);

    List<TaskDto> mapToDtos(List<Task> entities);

    List<Task> mapToEntities(List<TaskDto> dtos);

}
