package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.FarmerPerks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FarmerPerksRepository extends JpaRepository<FarmerPerks, Integer> {
}
