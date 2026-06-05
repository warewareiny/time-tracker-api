package com.example.timetracker.repository;

import com.example.timetracker.entity.Task;
import com.example.timetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByUser(User user);

    Optional<Task> findByIdAndUser(Integer id, User user);
}
