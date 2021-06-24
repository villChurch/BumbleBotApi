package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.Curding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CurdingRespoitory extends JpaRepository<Curding, Integer> {

    @Query(value = "SELECT * FROM curding WHERE expiryDate <= DATE_SUB(NOW() , INTERVAL 1 DAY)", nativeQuery=true)
    List<Curding> GetCurdledMilk();
}
