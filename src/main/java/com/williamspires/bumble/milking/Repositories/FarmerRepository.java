package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, String> {


}
