package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.MilkExpiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MilkExpiryRepository extends JpaRepository<MilkExpiry, Integer> {

    @Query(value = "SELECT * FROM milkexpiry WHERE expiryDate <= DATE_SUB(NOW() , INTERVAL 1 DAY)", nativeQuery=true)
    List<MilkExpiry> findExpiredMilk();

    List<MilkExpiry> findByDiscordID(String discordID);
}
