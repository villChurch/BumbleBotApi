package com.williamspires.bumble.milking.Repositories;

import com.williamspires.bumble.milking.models.CookingDoes;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class CookingDoesInsert {

    @PersistenceContext
    private EntityManager entitymanager;

    @Transactional
    public void startCooking(CookingDoes doe) {
        entitymanager.createNativeQuery("INSERT INTO cookingdoes (goatId, dueDate) values (?, ?)")
                .setParameter(1, doe.getGoatid())
                .setParameter(2, doe.getDueDate())
                .executeUpdate();
    }

    @Transactional
    public void insertWithEntityManager(CookingDoes doe) {
        this.entitymanager.persist(doe);
    }
}
