package com.example.timetracker.unit;

import com.example.timetracker.task.dto.CreateAndUpdateTaskRequest;
import com.example.timetracker.task.entity.Status;
import com.example.timetracker.task.entity.Task;
import com.example.timetracker.auth.entity.User;
import com.example.timetracker.task.exception.TaskNotFoundException;
import com.example.timetracker.task.repository.TaskRepository;
import com.example.timetracker.task.service.TaskService;
import com.example.timetracker.auth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;

    private static final User USER = User.builder()
            .username("test")
            .build();

    private static final CreateAndUpdateTaskRequest TASK_REQ =
            CreateAndUpdateTaskRequest.builder()
                    .title("new task")
                    .description("new description")
                    .status(Status.DONE)
                    .build();

    @BeforeEach
    void setUp() {
        when(userService.getCurrentUser()).thenReturn(USER);

        testTask = Task.builder()
                .title("task")
                .description("description")
                .status(Status.TODO)
                .user(USER)
                .build();
    }

    @Test
    void shouldReturnAllTasks() {
        when(taskRepository.findByUser(USER))
                .thenReturn(List.of(testTask));

        List<Task> result = taskService.findAll();

        assertThat(result)
                .hasSize(1)
                .containsExactly(testTask);
    }

    @Test
    void shouldFindTaskById() {
        when(taskRepository.findByIdAndUser(1, USER))
                .thenReturn(Optional.of(testTask));

        Task result = taskService.findById(1);

        assertThat(result).isEqualTo(testTask);
        assertThat(result.getTitle()).isEqualTo("task");
        assertThat(result.getDescription()).isEqualTo("description");
        assertThat(result.getStatus()).isEqualTo(Status.TODO);
    }

    @Test
    void shouldThrowWhenTaskNotFound() {
        when(taskRepository.findByIdAndUser(1, USER))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(1))
                .isInstanceOf(TaskNotFoundException.class);

        verify(taskRepository).findByIdAndUser(1, USER);
    }

    @Test
    void shouldDeleteTaskById() {
        when(taskRepository.findByIdAndUser(1, USER))
                .thenReturn(Optional.of(testTask));

        taskService.deleteById(1);

        verify(taskRepository).findByIdAndUser(1, USER);
        verify(taskRepository).delete(testTask);
    }

    @Test
    void shouldCreateTask() {
        Task savedTask = Task.builder()
                .title("new task")
                .description("new description")
                .status(Status.DONE)
                .user(USER)
                .build();

        when(taskRepository.save(any(Task.class)))
                .thenReturn(savedTask);

        Task result = taskService.create(TASK_REQ);

        assertThat(result.getTitle()).isEqualTo("new task");
        assertThat(result.getDescription()).isEqualTo("new description");
        assertThat(result.getStatus()).isEqualTo(Status.DONE);

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldUpdateTask() {
        when(taskRepository.findByIdAndUser(1, USER))
                .thenReturn(Optional.of(testTask));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.updateCurrentTask(1, TASK_REQ);

        assertThat(result.getTitle()).isEqualTo("new task");
        assertThat(result.getDescription()).isEqualTo("new description");
        assertThat(result.getStatus()).isEqualTo(Status.DONE);

        verify(taskRepository).save(testTask);
    }
}