package com.williamspires.bumble.milking.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "softcheeseexpiry")
@Getter
@Setter
public class SoftCheeseExpiry {

    @GeneratedValue
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "amount")
    private int amount;

    @Column(name = "expirydate")
    private String expirydate;

    @Column(name = "DiscordID")
    private String discordID;
}
