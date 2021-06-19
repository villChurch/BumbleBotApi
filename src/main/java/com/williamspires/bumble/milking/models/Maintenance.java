package com.williamspires.bumble.milking.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Maintenance")
@Getter
@Setter
public class Maintenance {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "farmerid")
    private String farmerid;

    @Column(name = "needsmaintenance")
    private boolean needsMaintenance;

    @Column(name = "milkingboost")
    private boolean milkingBoost;

    @Column(name = "dailyboost")
    private boolean dailyBoost;

}
