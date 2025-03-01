package org.example.factoryservice.dto;

import lombok.Data;

@Data
public class MachineRequestDto {
    private Long id;
    private String name;
    private String status;
}
