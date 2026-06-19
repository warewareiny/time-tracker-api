package com.example.timetracker.timeentry.controller;

import com.example.timetracker.timeentry.dto.ActiveTimerResponse;
import com.example.timetracker.timeentry.dto.TimeEntryResponse;
import com.example.timetracker.timeentry.dto.TimeStatisticsResponse;
import com.example.timetracker.timeentry.entity.TimeEntry;
import com.example.timetracker.timeentry.mapper.TimeEntryMapper;
import com.example.timetracker.timeentry.service.TimeEntryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/time-entries")
@RequiredArgsConstructor
public class TimeEntryController {

    private final TimeEntryService timeEntryService;
    private final TimeEntryMapper timeEntryMapper;

    @GetMapping("/statistics")
    public TimeStatisticsResponse getStatistics() {
        return timeEntryService.getStatistics();
    }

    @GetMapping
    public List<TimeEntryResponse> findAll() {
        return timeEntryService.findAll().stream()
                .map(timeEntryMapper::toTimeEntryResponse)
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/start/{taskId}")
    public ActiveTimerResponse start(@PathVariable Integer taskId) {
        TimeEntry timeEntry = timeEntryService.startTimer(taskId);
        return timeEntryMapper.toActiveTimerResponse(timeEntry);
    }

    @PostMapping("/stop")
    public ActiveTimerResponse stop() {
        TimeEntry timeEntry = timeEntryService.stopTimer();
        return timeEntryMapper.toActiveTimerResponse(timeEntry);
    }

    @GetMapping("/active")
    public ActiveTimerResponse active() {
        TimeEntry timeEntry = timeEntryService.getActiveTimer();
        return timeEntryMapper.toActiveTimerResponse(timeEntry);
    }

    @GetMapping("/active/minutes")
    public Long getMinutes() {
        return timeEntryService.getActiveTimerDuration();
    }

}
