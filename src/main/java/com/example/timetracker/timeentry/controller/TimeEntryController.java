package com.example.timetracker.timeentry.controller;

import com.example.timetracker.timeentry.dto.ActiveTimerResponse;
import com.example.timetracker.timeentry.dto.TimeEntryResponse;
import com.example.timetracker.timeentry.dto.TimeStatisticsResponse;
import com.example.timetracker.timeentry.service.TimeEntryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/time-entries")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TimeEntryController {

    private final TimeEntryService timeEntryService;

    @GetMapping("/statistics")
    public TimeStatisticsResponse getStatistics() {
        return timeEntryService.getStatistics();
    }

    @GetMapping
    public List<TimeEntryResponse> findAll() {
        return timeEntryService.findAll();
    }

    @PostMapping("/start/{taskId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ActiveTimerResponse start(@PathVariable Integer taskId) {
        return timeEntryService.startTimer(taskId);
    }

    @PostMapping("/stop")
    public ActiveTimerResponse stop() {
        return timeEntryService.stopTimer();
    }

    @GetMapping("/active")
    public ActiveTimerResponse active() {
        return timeEntryService.getActiveTimer();
    }

    @GetMapping("/active/minutes")
    public Long getMinutes() {
        return timeEntryService.getActiveTimerDuration();
    }
}