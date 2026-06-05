package com.example.timetracker.controller;

import com.example.timetracker.dto.ActiveTimerResponse;
import com.example.timetracker.entity.TimeEntry;
import com.example.timetracker.service.TimeEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/time-entries")
@RequiredArgsConstructor
public class TimeEntryController {

    private final TimeEntryService timeEntryService;

    @PostMapping("/start/{taskId}")
    public ActiveTimerResponse start(@PathVariable Integer taskId) {
        TimeEntry timeEntry = timeEntryService.startTimer(taskId);
        return toResponse(timeEntry);
    }

    @PostMapping("/stop")
    public ActiveTimerResponse stop() {
        TimeEntry timeEntry = timeEntryService.stopTimer();
        return toResponse(timeEntry);
    }

    @GetMapping("/active")
    public ActiveTimerResponse active() {
        TimeEntry timeEntry = timeEntryService.getActiveTimer();
        return toResponse(timeEntry);
    }

    @GetMapping("/active/minutes")
    public Long getMinutes() {
        return timeEntryService.getActiveTimerDuration();
    }

    private ActiveTimerResponse toResponse(TimeEntry timeEntry) {
        return ActiveTimerResponse.builder()
                .taskId(timeEntry.getTask().getId())
                .taskTitle(timeEntry.getTask().getTitle())
                .startTime(timeEntry.getStartTime())
                .durationMinutes(
                        timeEntry.getEndTime() == null
                                ? Duration.between(timeEntry.getStartTime(), Instant.now()).toMinutes()
                                : timeEntry.getDurationMinutes()
                )
                .build();
    }

}
