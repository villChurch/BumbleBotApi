package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.Aging;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class AgingInsertRepository {

    @PersistenceContext
    private EntityManager entitymanager;

    @Transactional
    public void insertApiEvent(Aging aging) {
        entitymanager.createNativeQuery("INSERT INTO aging (amount, DiscordID, expiryDate) values (?, ?, ?)")
                .setParameter(1, aging.getAmount())
                .setParameter(2, aging.getDiscordId())
                .setParameter(3, aging.getExpirydate())
                .executeUpdate();
    }

    @Transactional
    public void insertWithEntityManager(Aging aging) {
        this.entitymanager.persist(aging);
    }

}
