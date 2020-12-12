package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.CookingDoes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CookingDoesRepository extends JpaRepository<CookingDoes, Integer> {

    Integer countByGoatid(int goatId);

    @Query(value = "SELECT * FROM cookingdoes WHERE dueDate <= DATE_SUB(NOW() , INTERVAL 1 DAY)", nativeQuery=true)
    List<CookingDoes> doesReady();
}
