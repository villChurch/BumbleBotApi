package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.Milking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MilkingRepository extends JpaRepository<Milking, Integer> {

    Integer countByDiscordId(String discordId);
}
