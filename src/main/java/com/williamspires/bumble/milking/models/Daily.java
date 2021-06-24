package com.williamspires.bumble.milking.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "daily")
public class Daily {

    @Column(name = "id")
    @GeneratedValue
    @Id
    private int id;

    @Column(name = "DiscordID")
    private String discordID;
}
