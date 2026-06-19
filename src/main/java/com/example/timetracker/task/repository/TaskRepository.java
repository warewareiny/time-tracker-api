package com.example.timetracker.task.repository;

import com.example.timetracker.task.entity.Status;
import com.example.timetracker.task.entity.Task;
import com.example.timetracker.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByUser(User user);

    List<Task> findByUserAndStatus(User user, Status status);

    Optional<Task> findByIdAndUser(Integer id, User user);
}
