package com.williamspires.bumble.milking.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "cookingdoes")
public class CookingDoes {

    @Column(name = "id")
    @GeneratedValue
    @Id
    private int id;

    @Column(name = "goatid")
    private int goatid;

    @Column(name = "duedate")
    private String dueDate;

    @Column(name = "ready")
    private boolean ready;

}
