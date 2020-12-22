package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.Dairy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DairyRepository extends JpaRepository<Dairy, Integer> {

    Optional<Dairy> findByOwnerId(String ownerId);
}
