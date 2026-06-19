package com.example.timetracker.task.mapper;

import com.example.timetracker.task.dto.TaskResponse;
import com.example.timetracker.task.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    TaskResponse toTaskResponse(Task task);

    Task toTask(TaskResponse taskResponse);

    List<TaskResponse> toTaskResponses(List<Task> tasks);

    Page<TaskResponse> toPageTaskResponse(Page<Task> pageable);

}
