package com.example.timetracker.unit;

import com.example.timetracker.auth.entity.User;
import com.example.timetracker.auth.service.UserService;
import com.example.timetracker.task.dto.CreateAndUpdateTaskRequest;
import com.example.timetracker.task.entity.Status;
import com.example.timetracker.task.entity.Task;
import com.example.timetracker.task.exception.TaskNotFoundException;
import com.example.timetracker.task.repository.TaskRepository;
import com.example.timetracker.task.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
            new CreateAndUpdateTaskRequest("new task", "new description", Status.DONE);

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
    void shouldReturnPageOfTasks() {
        Pageable pageable = PageRequest.of(0, 10);

        User user = User.builder()
                .id(1)
                .username("test")
                .build();

        Task task = Task.builder()
                .id(1)
                .title("task")
                .build();

        Page<Task> page = new PageImpl<>(List.of(task), pageable, 1);

        when(userService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findByUser(user, pageable)).thenReturn(page);

        Page<Task> result = taskService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle())
                .isEqualTo("task");

        verify(userService).getCurrentUser();
        verify(taskRepository).findByUser(user, pageable);
    }

    @Test
    void shouldReturnTasksByStatus() {
        Task testTask2 = Task.builder()
                .title("task2")
                .description("description2")
                .status(Status.TODO)
                .user(USER)
                .build();

        when(userService.getCurrentUser()).thenReturn(USER);
        when(taskRepository.findByUserAndStatus(USER, Status.TODO)).thenReturn(List.of(testTask, testTask2));
        when(taskRepository.findByUserAndStatus(USER, Status.DONE)).thenReturn(List.of());

        List<Task> todoTasks = taskService.findAllByStatus(Status.TODO);
        List<Task> doneTasks = taskService.findAllByStatus(Status.DONE);

        assertThat(todoTasks).hasSize(2);
        assertThat(doneTasks).hasSize(0);

        verify(taskRepository).findByUserAndStatus(USER, Status.TODO);
        verify(taskRepository).findByUserAndStatus(USER, Status.DONE);
    }

    @Test
    void shouldReturnAllTasks() {
        when(taskRepository.findByUser(USER)).thenReturn(List.of(testTask));

        List<Task> result = taskService.findAll();

        assertThat(result).hasSize(1).containsExactly(testTask);
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