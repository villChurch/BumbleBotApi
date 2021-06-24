package com.williamspires.bumble.milking.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "newbornkids")
public class NewBornKids {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "baseColour")
    private String basecolour;

    @Column(name = "breed")
    private String breed;

    @Column(name = "level")
    private int level;

    @Column(name = "imageLink")
    private String imageLink;

    @Column(name = "mother")
    private int mother;

    @Column(name = "name")
    private String name;

    @Column(name = "ownerID")
    private String ownerId;
}
