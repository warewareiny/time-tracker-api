package com.example.timetracker.timeentry.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimeEntryResponse {

    Integer taskId;
    Long durationMinutes;

}
