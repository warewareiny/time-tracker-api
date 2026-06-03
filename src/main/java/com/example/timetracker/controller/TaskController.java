package com.example.timetracker.controller;

import com.example.timetracker.dto.CreateAndUpdateTaskRequest;
import com.example.timetracker.dto.TaskResponse;
import com.example.timetracker.entity.Task;
import com.example.timetracker.service.TaskService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<TaskResponse> getTasks() {
        return taskService.findAll().stream()
                .map(this::toResponse)
                .collect(toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public TaskResponse create(@RequestBody @Valid CreateAndUpdateTaskRequest taskRequest) {
        return toResponse(taskService.save(toTask(taskRequest)));
    }

    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable Integer id) {
        return toResponse(taskService.findById(id));
    }

    @PutMapping("/{id}")
    public TaskResponse updateCurrentTask(@PathVariable Integer id,
                                          @RequestBody @Valid CreateAndUpdateTaskRequest request) {
        return toResponse(taskService.updateCurrentTask(id, request));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        taskService.deleteById(id);
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .build();
    }

    private Task toTask(CreateAndUpdateTaskRequest request) {
        return Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .build();
    }
}
