package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.MilkExpiry;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class MilkExpiryRepositoryInsert {
    @PersistenceContext
    private EntityManager entitymanager;

    @Transactional
    public void insertExpiryEvent(MilkExpiry milkingEvent) {
        entitymanager.createNativeQuery("INSERT INTO milkexpiry (DiscordID, Amount, expiryDate) values (?,?,?)")
                .setParameter(1, milkingEvent.getDiscordID())
                .setParameter(2, milkingEvent.getMilk())
                .setParameter(3, milkingEvent.getExpirydate())
                .executeUpdate();
    }

}
