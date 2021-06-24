package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Integer> {

    Optional<Maintenance> findMaintenanceByFarmerid(String farmerId);
}
