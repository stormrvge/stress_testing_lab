package org.example.factoryservice.service;

import jakarta.transaction.Transactional;
import org.example.factoryservice.model.ExportSummary;
import org.example.factoryservice.model.Task;
import org.example.factoryservice.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private static final int TASKS_MINUTES_WINDOW = 5;
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Task createTask(Task task) {
        task.setCreatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    @Transactional
    public Optional<Task> completeTask(Long taskId) {
        return taskRepository.findById(taskId).map(task -> {
            task.setStatus("COMPLETED");
            return taskRepository.save(task);
        });
    }

    public List<Task> getTasks(int minutes) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusMinutes(minutes);
        return taskRepository.findByCreatedAtAfter(oneHourAgo);
    }

    public ExportSummary generateSummary() {
        List<Task> tasks = getTasks(TASKS_MINUTES_WINDOW);
        long active = tasks.stream().filter(t -> t.getStatus().equals("NEW")).count();
        long completed = tasks.stream().filter(t -> t.getStatus().equals("COMPLETED")).count();
        return new ExportSummary(active, completed);
    }
}
