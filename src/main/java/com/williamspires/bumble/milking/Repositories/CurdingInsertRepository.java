package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.Curding;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class CurdingInsertRepository {

    @PersistenceContext
    private EntityManager entitymanager;

    @Transactional
    public void insertApiEvent(Curding curding) {
        entitymanager.createNativeQuery("INSERT INTO curding (amount, DiscordID, expiryDate) values (?, ?, ?)")
                .setParameter(1, curding.getAmount())
                .setParameter(2, curding.getDiscordId())
                .setParameter(3, curding.getExpirydate())
                .executeUpdate();
    }

    @Transactional
    public void insertWithEntityManager(Curding curding) {
        this.entitymanager.persist(curding);
    }

}
