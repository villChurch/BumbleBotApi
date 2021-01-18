package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.Cave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaveRepository extends JpaRepository<Cave, String> {


}
