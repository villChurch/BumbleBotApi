package com.williamspires.bumble.milking.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "milking")
@Getter
@Setter
public class Milking {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "DiscordID")
    private String discordId;
}
