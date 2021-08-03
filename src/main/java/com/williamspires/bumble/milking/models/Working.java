package com.williamspires.bumble.milking.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "working")
@Getter
@Setter
public class Working {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "farmerid")
    private String farmerid;

    @Column(name = "starttime")
    private Timestamp starttime;
}
