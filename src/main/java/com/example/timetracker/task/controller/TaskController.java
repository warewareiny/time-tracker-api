package com.example.timetracker.task.controller;

import com.example.timetracker.task.dto.CreateAndUpdateTaskRequest;
import com.example.timetracker.task.dto.TaskResponse;
import com.example.timetracker.task.mapper.TaskMapper;
import com.example.timetracker.task.service.TaskService;
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
    private final TaskMapper taskMapper;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<TaskResponse> getTasks() {
        return taskService.findAll().stream()
                .map(taskMapper::toTaskResponse)
                .collect(toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public TaskResponse create(@RequestBody @Valid CreateAndUpdateTaskRequest request) {
        return taskMapper.toTaskResponse(taskService.create(request));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable Integer id) {
        return taskMapper.toTaskResponse(taskService.findById(id));
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public TaskResponse updateCurrentTask(@PathVariable Integer id,
                                          @RequestBody @Valid CreateAndUpdateTaskRequest request) {
        return taskMapper.toTaskResponse(taskService.updateCurrentTask(id, request));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        taskService.deleteById(id);
    }

}
