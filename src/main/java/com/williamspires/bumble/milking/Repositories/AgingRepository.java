package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.Aging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgingRepository extends JpaRepository<Aging, Integer> {

    @Query(value = "SELECT * FROM aging WHERE expiryDate <= DATE_SUB(NOW() , INTERVAL 1 DAY)", nativeQuery=true)
    List<Aging> GetAgedCheese();

    List<Aging> findAgingByDiscordId(final String discordId);
}
