package org.example.factoryservice.dto;

import lombok.Data;

@Data
public class TaskRequestDto {
    private Long id;
    private Long machineId;
    private String description;
    private String status;
}
