package mezyk.mateusz.app.tasks;

import mezyk.mateusz.app.tasks.core.data.exception.InvalidTaskRequestException;
import mezyk.mateusz.app.tasks.core.data.exception.TaskNotFoundException;
import mezyk.mateusz.app.tasks.core.data.model.Task;
import mezyk.mateusz.app.tasks.core.data.service.TaskService;
import mezyk.mateusz.app.tasks.integration.endpoint.TaskEndpoint;
import mezyk.mateusz.app.tasks.integration.model.CreateTaskDto;
import mezyk.mateusz.app.tasks.integration.model.TaskDto;
import mezyk.mateusz.app.tasks.integration.service.TaskMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskEndpoint.class)
public class EndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private TaskMapper taskMapper;

    @Test
    public void shouldReturn201WhenCreateTaskDtoIsValid() throws Exception {
        CreateTaskDto dto = new CreateTaskDto("Title", "Description");

        mockMvc.perform(post("/task/new").contentType(APPLICATION_JSON)
            .content(asJason(dto)))
            .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturn400AndMessageWhenCreateTaskDtoHasNullTitle() throws Exception {
        CreateTaskDto dto = new CreateTaskDto(null, "Description");

        mockMvc.perform(post("/task/new").contentType(APPLICATION_JSON)
            .content(asJason(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(Matchers.containsString("error in object 'createTaskDto' on field 'title'")))
            .andExpect(content().string(Matchers.containsString("rejected value [null]")))
            .andExpect(content().string(Matchers.containsString("must not be blank")));
    }

    @Test
    public void shouldReturn400AndMessageWhenCreateTaskDtoHasBlankTitle() throws Exception {
        CreateTaskDto dto = new CreateTaskDto("", "Description");

        mockMvc.perform(post("/task/new").contentType(APPLICATION_JSON)
            .content(asJason(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(Matchers.containsString("error in object 'createTaskDto' on field 'title'")))
            .andExpect(content().string(Matchers.containsString("rejected value []")))
            .andExpect(content().string(Matchers.containsString("must not be blank")));
    }

    @Test
    public void shouldReturn200GettingAllTasks() throws Exception {
        CreateTaskDto dto = new CreateTaskDto("Title", "Description");

        mockMvc.perform(get("/task").contentType(APPLICATION_JSON)
            .content(asJason(dto)))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldReturn200GettingAllActiveTasks() throws Exception {
        CreateTaskDto dto = new CreateTaskDto("Title", "Description");

        mockMvc.perform(get("/task/active").contentType(APPLICATION_JSON)
            .content(asJason(dto)))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldReturn200GettingAllCompletedTasks() throws Exception {
        CreateTaskDto dto = new CreateTaskDto("Title", "Description");

        mockMvc.perform(get("/task/completed").contentType(APPLICATION_JSON)
            .content(asJason(dto)))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldReturn200GettingTaskById() throws Exception {
        when(taskService.findTaskById(anyLong())).thenReturn(new Task());
        CreateTaskDto dto = new CreateTaskDto("Title", "Description");

        mockMvc.perform(get("/task/{id}", 1L).contentType(APPLICATION_JSON)
            .content(asJason(dto)))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldReturn404GettingTaskByIdWhenThereIsNoSuchTask() throws Exception {
        when(taskService.findTaskById(anyLong())).thenThrow(TaskNotFoundException.class);
        CreateTaskDto dto = new CreateTaskDto("Title", "Description");

        mockMvc.perform(get("/task/{id}", 1L).contentType(APPLICATION_JSON)
            .content(asJason(dto)))
            .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturn200UpdatingExistingTask() throws Exception {
        when(taskService.updateTask(any(Task.class), anyLong())).thenReturn(new Task());
        TaskDto dto = new TaskDto("Title", "Description", true, 1L);

        mockMvc.perform(put("/task/update/{id}", 1L).contentType(APPLICATION_JSON)
            .content(asJason(dto)))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldReturn404UpdatingTaskSupplyingNoId() throws Exception {
        TaskDto dto = new TaskDto("Title", "Description", true, 1L);

        mockMvc.perform(put("/task/update/").contentType(APPLICATION_JSON)
            .content(asJason(dto)))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void shouldReturn404UpdatingTaskSupplyingTaskDtoWithBlankTitle() throws Exception {
        TaskDto dto = new TaskDto("", "Description", true, 1L);

        mockMvc.perform(put("/task/update/{id}", 1L).contentType(APPLICATION_JSON)
            .content(asJason(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(Matchers.containsString("error in object 'taskDto' on field 'title'")))
            .andExpect(content().string(Matchers.containsString("rejected value []")))
            .andExpect(content().string(Matchers.containsString("must not be blank")));

    }

    @Test
    public void shouldReturn404UpdatingTaskSupplyingTaskDtoWithNullTitle() throws Exception {
        TaskDto dto = new TaskDto(null, "Description", true, 1L);

        mockMvc.perform(put("/task/update/{id}", 1L).contentType(APPLICATION_JSON)
            .content(asJason(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(Matchers.containsString("error in object 'taskDto' on field 'title'")))
            .andExpect(content().string(Matchers.containsString("rejected value [null]")))
            .andExpect(content().string(Matchers.containsString("must not be blank")));
    }

    @Test
    public void shouldReturn200DeletingTask() throws Exception {
        doNothing().when(taskService).deleteTaskById(anyLong());

        mockMvc.perform(delete("/task/delete/{id}", 1L))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldReturn405DeletingTaskSupplyingNoId() throws Exception {
        doNothing().when(taskService).deleteTaskById(anyLong());

        mockMvc.perform(delete("/task/delete/"))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void shouldReturn400DeletingNotExistingTask() throws Exception {
        doThrow(TaskNotFoundException.class).when(taskService).deleteTaskById(anyLong());

        mockMvc.perform(delete("/task/delete/{id}", 1L))
            .andExpect(status().isNotFound());
    }

    @Test void shouldReturnReturn200SettingTaskCompleted() throws Exception {
        mockMvc.perform(patch("/task/update/{id}/completed", 1L))
                .andExpect(status().isOk());
    }

    @Test void shouldReturnReturn200SettingTaskActive() throws Exception {
        mockMvc.perform(patch("/task/update/{id}/active", 1L))
                .andExpect(status().isOk());
    }

    @Test void shouldReturnReturn400SettingTaskCompletedWithInvalidId() throws Exception {
        when(taskService.markTaskCompleted(anyLong())).thenThrow(InvalidTaskRequestException.class);
        mockMvc.perform(patch("/task/update/{id}/completed", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test void shouldReturnReturn400SettingTaskActiveWithInvalidId() throws Exception {
        when(taskService.markTaskActive(anyLong())).thenThrow(InvalidTaskRequestException.class);
        mockMvc.perform(patch("/task/update/{id}/active", 1L))
                .andExpect(status().isBadRequest());
    }

    public static String asJason(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
