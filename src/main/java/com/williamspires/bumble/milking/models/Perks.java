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

    @Column(name = "perkName")
    private String perkName;

    @Column(name = "perkBounsText")
    private String perkBonusText;

    @Column(name = "perkCost")
    private int perkCost;

    @Column(name = "levelUnlocked")
    private int levelUnlocked;
}
