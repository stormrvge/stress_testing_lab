package org.example.factoryservice.controller;

import org.example.factoryservice.dto.TaskRequestDto;
import org.example.factoryservice.dto.TaskResponseDto;
import org.example.factoryservice.model.Task;
import org.example.factoryservice.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@RequestBody TaskRequestDto taskRequestDto) {
        Task createdTask = taskService.createTask(mapToTask(taskRequestDto));
        return ResponseEntity.ok(mapToResponse(createdTask));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponseDto> completeTask(@PathVariable Long id) {
        return taskService.completeTask(id)
            .map(task -> ResponseEntity.ok(mapToResponse(task)))
            .orElse(ResponseEntity.notFound().build());
    }

    private Task mapToTask(TaskRequestDto taskRequestDto) {
        Task task = new Task();
        task.setMachineId(taskRequestDto.getMachineId());
        task.setStatus(taskRequestDto.getStatus());
        task.setDescription(taskRequestDto.getDescription());
        task.setCreatedAt(LocalDateTime.now());
        return task;
    }

    private TaskResponseDto mapToResponse(Task task) {
        TaskResponseDto taskResponseDto = new TaskResponseDto();
        taskResponseDto.setId(task.getId());
        taskResponseDto.setMachineId(task.getMachineId());
        taskResponseDto.setStatus(task.getStatus());
        taskResponseDto.setDescription(task.getDescription());
        taskResponseDto.setCreatedAt(task.getCreatedAt());
        return taskResponseDto;
    }
}
