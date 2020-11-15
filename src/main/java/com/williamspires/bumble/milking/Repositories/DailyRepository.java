package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.Daily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyRepository extends JpaRepository<Daily, Integer> {

    Integer countByDiscordID(String DiscordID);
}
