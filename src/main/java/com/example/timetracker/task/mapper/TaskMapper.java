package com.example.timetracker.task.mapper;

import com.example.timetracker.task.dto.TaskResponse;
import com.example.timetracker.task.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    TaskResponse toTaskResponse(Task task);

    List<TaskResponse> toTaskResponses(List<Task> tasks);

}
