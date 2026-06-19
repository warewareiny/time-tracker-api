package com.example.timetracker.timeentry.service;

import com.example.timetracker.auth.entity.User;
import com.example.timetracker.auth.service.UserService;
import com.example.timetracker.task.dto.TaskResponse;
import com.example.timetracker.task.entity.Status;
import com.example.timetracker.task.entity.Task;
import com.example.timetracker.task.exception.TaskAlreadyCompletedException;
import com.example.timetracker.task.mapper.TaskMapper;
import com.example.timetracker.task.service.TaskService;
import com.example.timetracker.timeentry.dto.ActiveTimerResponse;
import com.example.timetracker.timeentry.dto.TimeEntryResponse;
import com.example.timetracker.timeentry.dto.TimeStatisticsResponse;
import com.example.timetracker.timeentry.entity.TimeEntry;
import com.example.timetracker.timeentry.exception.ActiveTimerAlreadyExistsException;
import com.example.timetracker.timeentry.exception.ActiveTimerNotFoundException;
import com.example.timetracker.timeentry.mapper.TimeEntryMapper;
import com.example.timetracker.timeentry.repository.TimeEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final TaskService taskService;
    private final TaskMapper taskMapper;
    private final TimeEntryMapper timeEntryMapper;
    private final UserService userService;

    public TimeStatisticsResponse getStatistics() {
        User currentUser = userService.getCurrentUser();
        List<Task> tasks = currentUser.getTasks();

        long totalMinutes = tasks.stream()
                .flatMap(t -> t.getTimeEntries().stream())
                .mapToLong(TimeEntry::getDurationMinutes)
                .sum();

        long completedTasks = tasks.stream()
                .filter(t -> t.getStatus() == Status.DONE)
                .count();

        return new TimeStatisticsResponse(totalMinutes, completedTasks);
    }

    public List<TimeEntryResponse> findAll() {
        User user = userService.getCurrentUser();

        return timeEntryRepository.findByUser(user).stream()
                .map(timeEntryMapper::toTimeEntryResponse)
                .toList();
    }

    @Transactional
    public ActiveTimerResponse startTimer(Integer taskId) {
        User user = userService.getCurrentUser();

        TaskResponse task = taskService.findById(taskId);

        if (task.getStatus() == Status.DONE) {
            throw new TaskAlreadyCompletedException(taskId);
        }

        if (timeEntryRepository.existsByUserAndEndTimeIsNull(user)) {
            throw new ActiveTimerAlreadyExistsException();
        }

        TimeEntry entry = TimeEntry.builder()
                .user(user)
                .task(taskMapper.toTask(task))
                .startTime(Instant.now())
                .build();

        return timeEntryMapper.toActiveTimerResponse(
                timeEntryRepository.save(entry)
        );
    }

    @Transactional
    public ActiveTimerResponse stopTimer() {
        User user = userService.getCurrentUser();

        TimeEntry entry = getActiveEntry(user);

        entry.setEndTime(Instant.now());
        entry.setDurationMinutes(
                Duration.between(entry.getStartTime(), entry.getEndTime()).toMinutes()
        );

        Task task = entry.getTask();

        if (task.getStatus() != Status.DONE) {
            task.setStatus(Status.DONE);
        }

        return timeEntryMapper.toActiveTimerResponse(
                timeEntryRepository.save(entry)
        );
    }

    public ActiveTimerResponse getActiveTimer() {
        User user = userService.getCurrentUser();

        return timeEntryMapper.toActiveTimerResponse(
                getActiveEntry(user)
        );
    }

    public Long getActiveTimerDuration() {
        User user = userService.getCurrentUser();

        TimeEntry entry = getActiveEntry(user);

        return Duration.between(entry.getStartTime(), Instant.now()).toMinutes();
    }

    private TimeEntry getActiveEntry(User user) {
        return timeEntryRepository.findByUserAndEndTimeIsNull(user)
                .orElseThrow(ActiveTimerNotFoundException::new);
    }
}