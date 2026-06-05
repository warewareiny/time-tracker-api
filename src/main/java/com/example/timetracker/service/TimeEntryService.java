package com.example.timetracker.service;

import com.example.timetracker.entity.Task;
import com.example.timetracker.entity.TimeEntry;
import com.example.timetracker.entity.User;
import com.example.timetracker.repository.TimeEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Duration;
import java.time.Instant;

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
        Task task = taskService.findById(taskId);

        if (!task.getUser().getId().equals(currentUser.getId())) {
//            TODO: replace with custom exception
            throw new RuntimeException("Access denied");
        }

        if (timeEntryRepository.existsByUserAndEndTimeIsNull(currentUser)) {
//            TODO: replace with custom exception
            throw new RuntimeException("Active timer already exists");
        }

        TimeEntry entry = TimeEntry.builder()
                .user(currentUser)
                .task(task)
                .startTime(Instant.now())
                .build();

        return timeEntryRepository.save(entry);
    }

    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public TimeEntry stopTimer() {
        User currentUser = userService.getCurrentUser();

        TimeEntry entry = getActiveEntry(currentUser);
        entry.setEndTime(Instant.now());

        entry.setDurationMinutes(
                Duration.between(
                        entry.getStartTime(),
                        entry.getEndTime()
                ).toMinutes()
        );

        return timeEntryRepository.save(entry);
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
                .orElseThrow(() -> new RuntimeException("Active timer not found"));
    }
}
