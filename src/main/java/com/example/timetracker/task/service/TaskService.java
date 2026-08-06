package com.example.timetracker.task.service;

import com.example.timetracker.auth.entity.User;
import com.example.timetracker.auth.service.UserService;
import com.example.timetracker.task.dto.CreateAndUpdateTaskRequest;
import com.example.timetracker.task.dto.TaskResponse;
import com.example.timetracker.task.entity.Status;
import com.example.timetracker.task.entity.Task;
import com.example.timetracker.task.exception.TaskNotFoundException;
import com.example.timetracker.task.mapper.TaskMapper;
import com.example.timetracker.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
    private final TaskMapper taskMapper;

    @Cacheable(value = "tasks", key = "#userId")
    public List<TaskResponse> getAll(Integer userId) {
        return taskRepository.findAllByUserId(userId)
                .stream()
                .map(taskMapper::toTaskResponse)
                .toList();
    }

    public Page<TaskResponse> findAll(Status status, Pageable pageable) {
        User user = userService.getCurrentUser();

        Page<Task> tasks = (status == null)
                ? taskRepository.findByUser(user, pageable)
                : taskRepository.findByUserAndStatus(user, status, pageable);

        return tasks.map(taskMapper::toTaskResponse);
    }

    @Cacheable(value = "tasks", key = "#id")
    @Transactional(readOnly = true)
    public TaskResponse findById(Integer id) {
        Task task = findEntityById(id);
        return taskMapper.toTaskResponse(task);
    }

    @CacheEvict(value = "tasks", key = "#userId")
    @Transactional
    public TaskResponse create(CreateAndUpdateTaskRequest request) {
        User user = userService.getCurrentUser();

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .user(user)
                .build();

        Task saved = taskRepository.save(task);

        log.info("Task '{}' created with id {}", saved.getTitle(), saved.getId());

        return taskMapper.toTaskResponse(saved);
    }

    @CachePut(value = "tasks", key = "#id")
    @Transactional
    public TaskResponse updateCurrentTask(Integer id, CreateAndUpdateTaskRequest request) {
        Task task = findEntityById(id);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());

        log.info("Task {} updated", id);

        return taskMapper.toTaskResponse(task);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    @Transactional
    public void deleteById(Integer id) {
        Task task = findEntityById(id);

        taskRepository.delete(task);

        log.info("Task {} deleted", id);
    }

    private Task findEntityById(Integer id) {
        User user = userService.getCurrentUser();

        return taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> {
                    log.warn("Task {} not found for current user", id);
                    return new TaskNotFoundException(id);
                });
    }
}