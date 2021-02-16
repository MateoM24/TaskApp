package mezyk.mateusz.app.tasks.integration.service;

import mezyk.mateusz.app.tasks.core.data.model.Task;
import mezyk.mateusz.app.tasks.integration.model.CreateTaskDto;
import mezyk.mateusz.app.tasks.integration.model.TaskDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskMapperImpl implements TaskMapper {

    private final ModelMapper modelMapper;

    public TaskMapperImpl() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    public TaskDto mapToDto(Task entity) {
        return modelMapper.map(entity, TaskDto.class);
    }

    @Override
    public Task mapToEntity(CreateTaskDto dto) {
        return modelMapper.map(dto, Task.class);
    }

    @Override
    public Task mapToEntity(TaskDto dto) {
        return modelMapper.map(dto, Task.class);
    }

    @Override
    public List<TaskDto> mapToDtos(List<Task> entities) {
        return entities.stream()
                .map(task -> modelMapper.map(task, TaskDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<Task> mapToEntities(List<TaskDto> dtos) {
        return dtos.stream()
                .map(task -> modelMapper.map(task, Task.class)).collect(Collectors.toList());
    }

}
