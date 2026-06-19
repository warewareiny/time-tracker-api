package com.example.timetracker.timeentry.repository;

import com.example.timetracker.timeentry.entity.TimeEntry;
import com.example.timetracker.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, Integer> {

    List<TimeEntry> findByUser(User user);

    boolean existsByUserAndEndTimeIsNull(User user);

    Optional<TimeEntry> findByUserAndEndTimeIsNull(User user);

}
