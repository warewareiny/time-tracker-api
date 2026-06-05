package com.example.timetracker.repository;

import com.example.timetracker.entity.TimeEntry;
import com.example.timetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, Integer> {

    boolean existsByUserAndEndTimeIsNull(User user);
    Optional<TimeEntry> findByUserAndEndTimeIsNull(User user);
}
