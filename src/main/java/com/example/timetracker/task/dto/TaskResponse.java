package com.example.timetracker.task.dto;

import com.example.timetracker.task.entity.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskResponse {

    private Integer id;
    private String title;
    private String description;
    private Status status;
}
