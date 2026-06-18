package com.example.timetracker.timeentry.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ActiveTimerResponse {

    private Integer taskId;

    private String taskTitle;

    private Instant startTime;

    private Long durationMinutes;
}
