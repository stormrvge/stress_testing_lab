package org.example.factoryservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MachineResponseDto {
    private Long id;
    private String name;
    private String status;
    private LocalDateTime createdAt;
}
