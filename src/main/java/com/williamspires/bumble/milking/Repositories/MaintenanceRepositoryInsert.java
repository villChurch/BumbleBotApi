package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.Maintenance;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class MaintenanceRepositoryInsert {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    private void insertMaintenanceEvent(Maintenance maintenance) {
        entityManager.createNativeQuery("INSERT INTO maintenance (farmerId) VALUES (?)")
                .setParameter(1, maintenance.getFarmerid())
                .executeUpdate();
    }

    @Transactional
    public void insertMaintenanceWithEntityManager(Maintenance maintenance) {
        this.entityManager.persist(maintenance);
    }

}
