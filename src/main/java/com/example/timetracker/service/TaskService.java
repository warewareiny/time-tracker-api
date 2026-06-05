package com.example.timetracker.service;

import com.example.timetracker.dto.CreateAndUpdateTaskRequest;
import com.example.timetracker.entity.Task;
import com.example.timetracker.entity.User;
import com.example.timetracker.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    public Task findById(Integer id) {
        User currentUser = userService.getCurrentUser();
        return taskRepository.findByIdAndUser(id, currentUser)
//                TODO: replace with custom TaskNotFoundException
                .orElseThrow(() -> {
                    log.warn("Task {} not found for current user", id);
                    return new RuntimeException("Task not found");
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
