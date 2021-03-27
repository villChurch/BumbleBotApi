package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.DeadGoats;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class DeadGoatInsetRepository {
    @PersistenceContext
    private EntityManager entitymanager;

    @Transactional
    public void insertWithEntityManager(DeadGoats deadGoats) {
        this.entitymanager.persist(deadGoats);
    }
}
