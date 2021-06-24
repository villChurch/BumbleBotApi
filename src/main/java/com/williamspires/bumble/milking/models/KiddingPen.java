package com.williamspires.bumble.milking.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "kiddingpens")
public class KiddingPen {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "ownerID")
    private String ownerId;

    @Column(name = "capacity")
    private int capacity;
}
