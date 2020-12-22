package com.williamspires.bumble.milking.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="dairy")
@Getter
@Setter
public class Dairy {

    @GeneratedValue
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "ownerID")
    private String ownerId;

    @Column(name = "milk")
    private double milk;

    @Column(name = "slots")
    private int slots;

    @Column(name = "softcheese")
    private double softcheese;

    @Column(name = "hardcheese")
    private double hardcheese;
}
