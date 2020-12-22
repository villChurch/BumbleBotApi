package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.SoftCheeseExpiry;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class SoftCheeseExpiryInsertRepository {

    @PersistenceContext
    private EntityManager entitymanager;

    @Transactional
    public void insertExpiryEvent(SoftCheeseExpiry softCheeseExpiry) {
        entitymanager.createNativeQuery("INSERT INTO softcheeseexpiry (amount, expiryDate, DiscordID) values (?,?,?)")
                .setParameter(1, softCheeseExpiry.getAmount())
                .setParameter(2, softCheeseExpiry.getExpiryDate())
                .setParameter(3, softCheeseExpiry.getDiscordID())
                .executeUpdate();
    }

    @Transactional
    public void insertWithEntityManager(SoftCheeseExpiry softCheeseExpiry) {
        this.entitymanager.persist(softCheeseExpiry);
    }
}
