package com.example.timetracker.repository;

import com.example.timetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<User, Integer> {

}
