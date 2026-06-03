package com.example.timetracker.dto;

import com.example.timetracker.entity.Status;
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
