package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.SoftCheeseExpiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoftCheeseExpiryRepository extends JpaRepository<SoftCheeseExpiry, Integer> {

    @Query(value = "SELECT * FROM softcheeseexpiry WHERE expiryDate <= DATE_SUB(NOW() , INTERVAL 1 DAY)", nativeQuery=true)
    List<SoftCheeseExpiry> GetRottenSoftCheese();

    List<SoftCheeseExpiry> findAllByDiscordID(String discordID);
}
