package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.Goats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoatRepository extends JpaRepository<Goats, Integer> {

    List<Goats> findGoatsByOwnerId(String ownerId);
}
