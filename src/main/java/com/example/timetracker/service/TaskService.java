package com.example.timetracker.service;

import com.example.timetracker.dto.CreateAndUpdateTaskRequest;
import com.example.timetracker.entity.Task;
import com.example.timetracker.entity.User;
import com.example.timetracker.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    public Task findById(Integer id) {
        User currentUser = userService.getCurrentUser();
        return taskRepository.findByIdAndUser(id, currentUser)
//                TODO: replace with custom TaskNotFoundException
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public List<Task> findAll() {
        User currentUser = userService.getCurrentUser();
        return taskRepository.findByUser(currentUser);
    }

    @Transactional
    public void deleteById(Integer id) {
        Task task = findById(id);
        taskRepository.delete(task);
    }

    @Transactional
    public Task updateCurrentTask(Integer id, CreateAndUpdateTaskRequest request) {
        Task task = findById(id);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());

        return taskRepository.save(task);
    }

    @Transactional
    public Task create(CreateAndUpdateTaskRequest request) {
        User currentUser = userService.getCurrentUser();

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .user(currentUser)
                .build();

        return taskRepository.save(task);
    }
}
