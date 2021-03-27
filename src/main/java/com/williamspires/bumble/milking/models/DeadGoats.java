package com.williamspires.bumble.milking.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Table(name="deadgoats")
@Entity
@Getter
@Setter
public class DeadGoats {

    @Id
    @GeneratedValue
    @Column(name = "goatid")
    private int goatid;

    @Column(name = "baseColour")
    private String baseColour;

    @Column(name = "breed")
    private String breed;

    @Column(name = "level")
    private int level;

    @Column(name = "name")
    private String name;

    @Column(name = "ownerID")
    private String ownerId;

    @Column(name =  "imageLink")
    private String imageLink;
}
