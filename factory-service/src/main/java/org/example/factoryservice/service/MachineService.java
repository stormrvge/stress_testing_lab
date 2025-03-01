package org.example.factoryservice.service;

import org.example.factoryservice.model.Machine;
import org.example.factoryservice.repository.MachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MachineService {

    private final MachineRepository machineRepository;

    @Autowired
    public MachineService(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    public Machine saveMachine(Machine machine) {
        return machineRepository.save(machine);
    }

    public Optional<Machine> getMachineById(Long id) {
        return machineRepository.findById(id);
    }

    public Optional<Machine> updateMachineStatus(Long id, String status) {
        return machineRepository.findById(id).map(machine -> {
            machine.setStatus(status);
            return machineRepository.save(machine);
        });
    }
}
