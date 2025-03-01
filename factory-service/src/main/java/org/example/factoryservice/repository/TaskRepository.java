package org.example.factoryservice.repository;

import org.example.factoryservice.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCreatedAtAfter(LocalDateTime date);
}
