package com.example.timetracker.service;

import com.example.timetracker.dto.CreateAndUpdateTaskRequest;
import com.example.timetracker.entity.Task;
import com.example.timetracker.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public Task findById(Integer id) {
        return taskRepository.findById(id)
//                TODO: replace with custom TaskNotFoundException
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public void deleteById(Integer id) {
        taskRepository.deleteById(id);
    }

    public Task updateCurrentTask(Integer id, CreateAndUpdateTaskRequest taskRequest) {
        Task task = findById(id);

        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus());

        return save(task);
    }
}
