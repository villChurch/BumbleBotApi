package com.williamspires.bumble.milking.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Table(name = "goats")
@Entity
@Getter
@Setter
public class Goats {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name =  "level")
    private int level;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "breed")
    private String breed;

    @Column(name = "basecolour")
    private String basecolour;

    @Column(name = "levelmultiplier")
    private int levelmultiplier;

    @Column(name = "ownerID")
    private String ownerId;

    @Column(name = "equipped")
    private boolean equipped;

    @Column(name = "experience")
    private double experience;
}
