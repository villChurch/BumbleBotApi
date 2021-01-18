package com.williamspires.bumble.milking.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "dairycave")
public class Cave {

    @Id
    @Column(name = "ownerID")
    private String ownerId;

    @Column(name = "softcheese")
    private double softcheese;

    @Column(name = "slots")
    private int slots;

}
