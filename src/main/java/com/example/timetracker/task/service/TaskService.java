package com.example.timetracker.task.service;

import com.example.timetracker.auth.entity.User;
import com.example.timetracker.auth.service.UserService;
import com.example.timetracker.task.dto.CreateAndUpdateTaskRequest;
import com.example.timetracker.task.entity.Status;
import com.example.timetracker.task.entity.Task;
import com.example.timetracker.task.exception.TaskNotFoundException;
import com.example.timetracker.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    public Page<Task> findAll(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return taskRepository.findByUser(currentUser, pageable);
    }

    public List<Task> findAllByStatus(Status status) {
        User currentUser = userService.getCurrentUser();
        return taskRepository.findByUserAndStatus(currentUser, status);
    }

    public Task findById(Integer id) {
        User currentUser = userService.getCurrentUser();
        return taskRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> {
                    log.warn("Task {} not found for current user", id);
                    return new TaskNotFoundException(id);
                });
    }

    public List<Task> findAll() {
        User currentUser = userService.getCurrentUser();
        List<Task> tasks = taskRepository.findByUser(currentUser);

        log.info("Found {} tasks for user {}", tasks.size(), currentUser.getUsername());

        return tasks;
    }

    @Transactional
    public void deleteById(Integer id) {
        log.info("Starting deleting task {}", id);

        Task task = findById(id);
        taskRepository.delete(task);

        log.info("Task {} deleted", id);
    }

    @Transactional
    public Task updateCurrentTask(Integer id, CreateAndUpdateTaskRequest request) {
        log.info("Starting updating task {}", id);

        Task task = findById(id);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());

        log.info("Task {} status changed from {} to {}",
                id,
                task.getStatus(),
                request.getStatus());

        return taskRepository.save(task);
    }

    @Transactional
    public Task create(CreateAndUpdateTaskRequest request) {
        log.info("Starting creating task with title {}", request.getTitle());

        User currentUser = userService.getCurrentUser();

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .user(currentUser)
                .build();

        Task savedTask = taskRepository.save(task);

        log.info("Task {} created with id {}", savedTask.getTitle(), savedTask.getId());

        return savedTask;
    }
}
