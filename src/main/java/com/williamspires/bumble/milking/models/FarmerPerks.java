package com.williamspires.bumble.milking.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "farmerperks")
@Getter
@Setter
public class FarmerPerks {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "farmerid")
    private String farmerid;

    @Column(name = "perkid")
    private int perkid;
}
