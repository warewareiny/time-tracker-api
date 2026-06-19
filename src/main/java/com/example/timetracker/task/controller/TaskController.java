package com.example.timetracker.task.controller;

import com.example.timetracker.task.dto.CreateAndUpdateTaskRequest;
import com.example.timetracker.task.dto.TaskResponse;
import com.example.timetracker.task.entity.Status;
import com.example.timetracker.task.service.TaskService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public Page<TaskResponse> getTasks(@RequestParam(required = false) Status status,
                                       Pageable pageable) {
        return taskService.findAll(status, pageable);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public TaskResponse create(@RequestBody @Valid CreateAndUpdateTaskRequest request) {
        return taskService.create(request);
    }

    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable Integer id) {
        return taskService.findById(id);
    }

    @PutMapping("/{id}")
    public TaskResponse updateCurrentTask(@PathVariable Integer id,
                                          @RequestBody @Valid CreateAndUpdateTaskRequest request) {
        return taskService.updateCurrentTask(id, request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        taskService.deleteById(id);
    }

}
