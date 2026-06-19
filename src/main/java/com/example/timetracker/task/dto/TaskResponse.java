package com.example.timetracker.task.dto;

import com.example.timetracker.task.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Integer id;
    private String title;
    private String description;
    private Status status;

}
