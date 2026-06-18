package com.example.timetracker.unit;

import com.example.timetracker.entity.*;
import com.example.timetracker.timeentry.exception.ActiveTimerAlreadyExistsException;
import com.example.timetracker.timeentry.exception.ActiveTimerNotFoundException;
import com.example.timetracker.task.exception.TaskAlreadyCompletedException;
import com.example.timetracker.timeentry.entity.TimeEntry;
import com.example.timetracker.timeentry.repository.TimeEntryRepository;
import com.example.timetracker.task.entity.Status;
import com.example.timetracker.task.entity.Task;
import com.example.timetracker.task.service.TaskService;
import com.example.timetracker.timeentry.service.TimeEntryService;
import com.example.timetracker.auth.entity.User;
import com.example.timetracker.auth.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeEntryServiceTest {

    @Mock
    private TimeEntryRepository timeEntryRepository;

    @Mock
    private TaskService taskService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TimeEntryService timeEntryService;

    private final User user = User.builder()
            .id(1)
            .username("name")
            .email("email")
            .build();

    private final Task task = Task.builder()
            .id(1)
            .title("title")
            .description("desc")
            .status(Status.TODO)
            .build();

    @Test
    void shouldStartTimer() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(taskService.findById(1)).thenReturn(task);
        when(timeEntryRepository.existsByUserAndEndTimeIsNull(user)).thenReturn(false);

        when(timeEntryRepository.save(any(TimeEntry.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        TimeEntry result = timeEntryService.startTimer(1);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(task, result.getTask());
        assertNotNull(result.getStartTime());

        verify(timeEntryRepository).save(any(TimeEntry.class));
    }

    @Test
    void shouldThrowIfTaskCompleted() {
        task.setStatus(Status.DONE);

        when(userService.getCurrentUser()).thenReturn(user);
        when(taskService.findById(1)).thenReturn(task);

        assertThrows(TaskAlreadyCompletedException.class,
                () -> timeEntryService.startTimer(1));
    }

    @Test
    void shouldThrowIfActiveTimerExists() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(taskService.findById(1)).thenReturn(task);
        when(timeEntryRepository.existsByUserAndEndTimeIsNull(user)).thenReturn(true);

        assertThrows(ActiveTimerAlreadyExistsException.class,
                () -> timeEntryService.startTimer(1));
    }

    @Test
    void shouldStopTimer() {
        TimeEntry entry = TimeEntry.builder()
                .user(user)
                .task(task)
                .startTime(Instant.now().minusSeconds(120))
                .build();

        when(userService.getCurrentUser()).thenReturn(user);
        when(timeEntryRepository.findByUserAndEndTimeIsNull(user))
                .thenReturn(Optional.of(entry));

        when(timeEntryRepository.save(any(TimeEntry.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        TimeEntry result = timeEntryService.stopTimer();

        assertNotNull(result.getEndTime());
        assertNotNull(result.getDurationMinutes());
        assertEquals(Status.DONE, result.getTask().getStatus());

        verify(timeEntryRepository).save(entry);
    }

    @Test
    void shouldThrowIfNoActiveTimer() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(timeEntryRepository.findByUserAndEndTimeIsNull(user)).thenReturn(Optional.empty());

        assertThrows(ActiveTimerNotFoundException.class,
                () -> timeEntryService.stopTimer());
    }

    @Test
    void shouldGetActiveTimer() {
        TimeEntry entry = new TimeEntry();
        entry.setUser(user);

        when(userService.getCurrentUser()).thenReturn(user);
        when(timeEntryRepository.findByUserAndEndTimeIsNull(user))
                .thenReturn(Optional.of(entry));

        TimeEntry result = timeEntryService.getActiveTimer();

        assertEquals(entry, result);
    }

    @Test
    void shouldGetActiveTimerDuration() {
        TimeEntry entry = TimeEntry.builder()
                .user(user)
                .startTime(Instant.now().minusSeconds(120))
                .build();

        when(userService.getCurrentUser()).thenReturn(user);
        when(timeEntryRepository.findByUserAndEndTimeIsNull(user))
                .thenReturn(Optional.of(entry));

        Long duration = timeEntryService.getActiveTimerDuration();

        assertNotNull(duration);
        assertTrue(duration >= 1);
    }
}