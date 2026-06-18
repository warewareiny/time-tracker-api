package com.example.timetracker.timeentry.controller;

import com.example.timetracker.timeentry.dto.ActiveTimerResponse;
import com.example.timetracker.timeentry.dto.TimeEntryResponse;
import com.example.timetracker.timeentry.entity.TimeEntry;
import com.example.timetracker.timeentry.service.TimeEntryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/time-entries")
@RequiredArgsConstructor
public class TimeEntryController {

    private final TimeEntryService timeEntryService;

    @GetMapping
    public List<TimeEntryResponse> findAll() {
        return timeEntryService.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @PostMapping("/start/{taskId}")
    public ActiveTimerResponse start(@PathVariable Integer taskId) {
        TimeEntry timeEntry = timeEntryService.startTimer(taskId);
        return toActiveTimerResponse(timeEntry);
    }

    @PostMapping("/stop")
    public ActiveTimerResponse stop() {
        TimeEntry timeEntry = timeEntryService.stopTimer();
        return toActiveTimerResponse(timeEntry);
    }

    @GetMapping("/active")
    public ActiveTimerResponse active() {
        TimeEntry timeEntry = timeEntryService.getActiveTimer();
        return toActiveTimerResponse(timeEntry);
    }

    @GetMapping("/active/minutes")
    public Long getMinutes() {
        return timeEntryService.getActiveTimerDuration();
    }

    private ActiveTimerResponse toActiveTimerResponse(TimeEntry timeEntry) {
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

    private TimeEntryResponse toResponse(TimeEntry entry) {
        return TimeEntryResponse.builder()
                .taskId(entry.getTask().getId())
                .durationMinutes(entry.getDurationMinutes())
                .build();
    }

}
