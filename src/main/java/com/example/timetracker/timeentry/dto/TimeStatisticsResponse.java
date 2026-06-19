package com.example.timetracker.timeentry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimeStatisticsResponse {

    long totalMinutes;

    long completedTasks;

}