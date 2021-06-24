package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.Items;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemsRepository extends JpaRepository<Items, Integer> {

    Items findByOwnerIdAndName(String ownerId, String name);
}
