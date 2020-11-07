package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.Milking;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class MilkingRepositoryInsert {

    @PersistenceContext
    private EntityManager entitymanager;

    @Transactional
    public void insertApiEvent(Milking milkingEvent) {
        entitymanager.createNativeQuery("INSERT INTO milking (DiscordID) values (?)")
                .setParameter(1, milkingEvent.getDiscordId())
                .executeUpdate();
    }

    @Transactional
    public void insertWithEntityManager(Milking milkingEvent) {
        this.entitymanager.persist(milkingEvent);
    }
}
