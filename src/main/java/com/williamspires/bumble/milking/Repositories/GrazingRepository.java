package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.grazing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrazingRepository extends JpaRepository<grazing, Integer> {

    List<grazing> findByFarmerId(String farmerId);
}
