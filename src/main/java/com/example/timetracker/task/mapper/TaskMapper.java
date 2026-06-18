package com.example.timetracker.task.mapper;

import com.example.timetracker.task.dto.TaskResponse;
import com.example.timetracker.task.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    TaskResponse toTaskResponse(Task task);

}
