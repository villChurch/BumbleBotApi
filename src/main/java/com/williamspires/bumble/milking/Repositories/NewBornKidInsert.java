package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.CookingDoes;
import com.williamspires.bumble.milking.models.NewBornKids;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class NewBornKidInsert {

    @PersistenceContext
    private EntityManager entitymanager;

    @Transactional
    public void saveKiddToDB(NewBornKids kidd) {
        entitymanager.createNativeQuery("INSERT INTO newbornkids (baseColour, breed, level, name, imageLink, " +
                "ownerID, mother) values (?, ?, ?, ?, ?, ?, ?)")
                .setParameter(1, kidd.getBasecolour())
                .setParameter(2, kidd.getBreed())
                .setParameter(3, kidd.getLevel())
                .setParameter(4, kidd.getName())
                .setParameter(5, kidd.getImageLink())
                .setParameter(6, kidd.getOwnerId())
                .setParameter(7, kidd.getMother())
                .executeUpdate();
    }

    @Transactional
    public void insertWithEntityManager(NewBornKids kidd) {
        this.entitymanager.persist(kidd);
    }
}
