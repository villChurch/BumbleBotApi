package com.williamspires.bumble.milking.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.sql.Date;

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
