package com.example.timetracker.task.controller;

import com.example.timetracker.task.dto.CreateAndUpdateTaskRequest;
import com.example.timetracker.task.dto.TaskResponse;
import com.example.timetracker.task.entity.Status;
import com.example.timetracker.task.entity.Task;
import com.example.timetracker.task.mapper.TaskMapper;
import com.example.timetracker.task.service.TaskService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    private final TaskMapper taskMapper;

    @GetMapping
    public Page<TaskResponse> getTasks(Pageable pageable) {
        return taskService.findAll(pageable)
                .map(taskMapper::toTaskResponse);
    }

    @GetMapping
    public List<TaskResponse> getTasks(@RequestParam(required = false) Status status) {
        List<Task> tasks = status == null
                ? taskService.findAll()
                : taskService.findAllByStatus(status);

        return taskMapper.toTaskResponses(tasks);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public TaskResponse create(@RequestBody @Valid CreateAndUpdateTaskRequest request) {
        return taskMapper.toTaskResponse(taskService.create(request));
    }

    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable Integer id) {
        return taskMapper.toTaskResponse(taskService.findById(id));
    }

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
