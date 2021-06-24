package com.williamspires.bumble.milking.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "curding")
@Getter
@Setter
public class Curding {

    @GeneratedValue
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "amount")
    private double amount;

    @Column(name = "DiscordID")
    private String discordId;

    @Column(name = "expirydate")
    private String expirydate;
}
