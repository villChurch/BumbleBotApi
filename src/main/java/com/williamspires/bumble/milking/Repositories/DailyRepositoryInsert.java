package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.Daily;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class DailyRepositoryInsert {

    @PersistenceContext
    private EntityManager entitymanager;

    @Transactional
    public void insertApiEvent(Daily daily) {
        entitymanager.createNativeQuery("INSERT INTO daily (DiscordID) values (?)")
                .setParameter(1, daily.getDiscordID())
                .executeUpdate();
    }

}
