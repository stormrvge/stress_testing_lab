package org.example.factoryservice.controller;

import org.example.factoryservice.dto.MachineRequestDto;
import org.example.factoryservice.dto.MachineResponseDto;
import org.example.factoryservice.model.Machine;
import org.example.factoryservice.service.MachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/machines")
public class MachineController {

    private final MachineService machineService;

    @Autowired
    public MachineController(MachineService machineService) {
        this.machineService = machineService;
    }

    @PostMapping
    public ResponseEntity<MachineResponseDto> addMachine(@RequestBody MachineRequestDto machine) {
        Machine savedMachine = machineService.saveMachine(mapToMachine(machine));
        return ResponseEntity.ok(mapToResponse(savedMachine));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MachineResponseDto> getMachine(@PathVariable Long id) {
        return machineService.getMachineById(id)
            .map(machine -> ResponseEntity.ok(mapToResponse(machine)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MachineResponseDto> updateStatus(
        @PathVariable Long id,
        @RequestParam String status
    ) {
        return machineService.updateMachineStatus(id, status)
            .map(machine -> ResponseEntity.ok(mapToResponse(machine)))
            .orElse(ResponseEntity.notFound().build());
    }

    private Machine mapToMachine(MachineRequestDto machineRequestDto) {
        Machine machine = new Machine();
        machine.setName(machineRequestDto.getName());
        machine.setStatus(machineRequestDto.getStatus());
        machine.setCreatedAt(LocalDateTime.now());
        return machine;
    }

    private MachineResponseDto mapToResponse(Machine machine) {
        MachineResponseDto machineResponseDto = new MachineResponseDto();
        machineResponseDto.setId(machine.getId());
        machineResponseDto.setName(machine.getName());
        machineResponseDto.setStatus(machine.getStatus());
        machineResponseDto.setCreatedAt(machine.getCreatedAt());
        return machineResponseDto;
    }
}
