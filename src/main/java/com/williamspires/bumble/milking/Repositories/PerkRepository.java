package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.Perks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerkRepository extends JpaRepository<Perks, Integer> {
}
