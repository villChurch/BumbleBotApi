package com.williamspires.bumble.milking.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "milkexpiry")
@Getter
@Setter
public class MilkExpiry {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "DiscordID")
    private String discordID;

    @Column(name = "amount")
    private double milk;

    @Column(name = "expirydate")
    private String expirydate;
}
