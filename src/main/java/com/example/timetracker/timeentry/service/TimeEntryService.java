package com.example.timetracker.timeentry.service;

import com.example.timetracker.auth.entity.User;
import com.example.timetracker.auth.service.UserService;
import com.example.timetracker.task.entity.Status;
import com.example.timetracker.task.entity.Task;
import com.example.timetracker.task.exception.TaskAlreadyCompletedException;
import com.example.timetracker.task.service.TaskService;
import com.example.timetracker.timeentry.dto.TimeStatisticsResponse;
import com.example.timetracker.timeentry.entity.TimeEntry;
import com.example.timetracker.timeentry.exception.ActiveTimerAlreadyExistsException;
import com.example.timetracker.timeentry.exception.ActiveTimerNotFoundException;
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
    private final UserService userService;

    public TimeStatisticsResponse getStatistics() {
        User currentUser = userService.getCurrentUser();
        List<Task> tasks = currentUser.getTasks();

        long totalMinutes = tasks.stream()
                .flatMap(task -> task.getTimeEntries().stream())
                .mapToLong(TimeEntry::getDurationMinutes)
                .sum();

        long completedTasks = tasks.stream()
                .filter(task -> task.getStatus() == Status.DONE)
                .count();

        return new TimeStatisticsResponse(totalMinutes, completedTasks);
    }

    @Transactional
    public TimeEntry startTimer(Integer taskId) {
        User currentUser = userService.getCurrentUser();

        log.info("Starting timer for task {} by user {}", taskId, currentUser.getId());

        Task task = taskService.findById(taskId);

        if (task.getStatus() == Status.DONE) {
            log.warn("User {} tried to start completed task {}", currentUser.getId(), taskId);
            throw new TaskAlreadyCompletedException(taskId);
        }

        if (timeEntryRepository.existsByUserAndEndTimeIsNull(currentUser)) {
            log.warn("User {} already has active timer", currentUser.getId());
            throw new ActiveTimerAlreadyExistsException();
        }

        TimeEntry entry = TimeEntry.builder()
                .user(currentUser)
                .task(task)
                .startTime(Instant.now())
                .build();

        TimeEntry savedEntry = timeEntryRepository.save(entry);

        log.info("Timer {} started for task {}", savedEntry.getId(), taskId);

        return savedEntry;
    }

    @Transactional
    public TimeEntry stopTimer() {
        User currentUser = userService.getCurrentUser();

        log.info("Stopping active timer for user {}", currentUser.getId());

        TimeEntry entry = getActiveEntry(currentUser);

        entry.setEndTime(Instant.now());
        entry.setDurationMinutes(
                Duration.between(
                        entry.getStartTime(),
                        entry.getEndTime()
                ).toMinutes()
        );

        Task task = entry.getTask();

        if (task.getStatus() != Status.DONE) {
            task.setStatus(Status.DONE);

            log.info("Task {} automatically marked as DONE", task.getId());
        }

        TimeEntry savedEntry = timeEntryRepository.save(entry);

        log.info("Timer {} stopped. Duration {} minutes", savedEntry.getId(), savedEntry.getDurationMinutes());

        return savedEntry;
    }

    public List<TimeEntry> findAll() {
        User currentUser = userService.getCurrentUser();
        return timeEntryRepository.findByUser(currentUser);
    }

    public Long getActiveTimerDuration() {
        User currentUser = userService.getCurrentUser();

        TimeEntry entry = getActiveEntry(currentUser);

        return Duration.between(
                entry.getStartTime(),
                Instant.now()
        ).toMinutes();
    }

    public TimeEntry getActiveTimer() {
        User currentUser = userService.getCurrentUser();
        return getActiveEntry(currentUser);
    }

    private TimeEntry getActiveEntry(User user) {
        return timeEntryRepository.findByUserAndEndTimeIsNull(user)
                .orElseThrow(() -> {
                    log.warn("Active timer not found for user {}", user.getId());
                    return new ActiveTimerNotFoundException();
                });
    }
}
