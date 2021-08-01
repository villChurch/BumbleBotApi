package com.williamspires.bumble.milking.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "perks")
@Getter
@Setter
public class Perks {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "perkname")
    private String perkname;

    @Column(name = "perkbonustext")
    private String perkbonustext;

    @Column(name = "perkcost")
    private int perkcost;

    @Column(name = "levelunlocked")
    private int levelunlocked;

    @Column(name = "requires")
    private int requires;
}
