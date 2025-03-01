package org.example.factoryservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskResponseDto {
    private Long id;
    private Long machineId;
    private String description;
    private String status;
    private LocalDateTime createdAt;
}
