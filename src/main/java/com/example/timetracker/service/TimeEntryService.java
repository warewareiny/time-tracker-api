package com.example.timetracker.service;

import com.example.timetracker.entity.Task;
import com.example.timetracker.entity.TimeEntry;
import com.example.timetracker.entity.User;
import com.example.timetracker.repository.TimeEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final TaskService taskService;
    private final UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public TimeEntry startTimer(Integer taskId) {
        User currentUser = userService.getCurrentUser();

        log.info("Starting timer for task {} by user {}", taskId, currentUser.getId());

        Task task = taskService.findById(taskId);

        if (!task.getUser().getId().equals(currentUser.getId())) {
            log.warn("User {} tried to start timer for alien task {}", currentUser.getId(), taskId);
//            TODO: replace with custom exception
            throw new RuntimeException("Access denied");
        }

        if (timeEntryRepository.existsByUserAndEndTimeIsNull(currentUser)) {
            log.warn("User {} already has active timer", currentUser.getId());
//            TODO: replace with custom exception
            throw new RuntimeException("Active timer already exists");
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

    @ResponseStatus(HttpStatus.OK)
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

        TimeEntry savedEntry = timeEntryRepository.save(entry);

        log.info("Timer {} stopped. Duration {} minutes", savedEntry.getId(), savedEntry.getDurationMinutes());

        return savedEntry;
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
//                    TODO: replace with custom exception
                    log.warn("Active timer not found for user {}", user.getId());
                    return new RuntimeException("Active timer not found");
                });
    }
}
