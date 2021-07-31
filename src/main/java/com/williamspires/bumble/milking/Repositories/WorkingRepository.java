package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.Working;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkingRepository extends JpaRepository<Working, Integer> {

    Working findFirstByFarmerid(String farmerId);
}
