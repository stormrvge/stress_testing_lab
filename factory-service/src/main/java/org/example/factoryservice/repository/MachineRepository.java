package org.example.factoryservice.repository;

import org.example.factoryservice.model.Machine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MachineRepository extends JpaRepository<Machine, Long> {
    Optional<Machine> findById(Long id);
}
