package com.williamspires.bumble.milking.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "farmers")
@Getter
@Setter
public class Farmer {

    @Id
    @Column(name = "DiscordID")
    private String DiscordID;

    @Column(name = "credits")
    private int credits;

    @Column(name = "barnsize")
    private int barn_size;

    @Column(name = "grazesize")
    private int grazing_size;

    @Column(name = "milk")
    private double milk;

    @Column(name = "oats")
    private boolean oats;

    @Column(name = "perkpoints")
    private int perkpoints;

    @Column(name = "working")
    private boolean working;

    @Column(name = "level")
    private int level;

    @Column(name = "experience")
    private double experience;
}
