package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.KiddingPen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KiddingPenRepository extends JpaRepository<KiddingPen, Integer> {

    List<KiddingPen> findByOwnerId(String ownerId);
}
