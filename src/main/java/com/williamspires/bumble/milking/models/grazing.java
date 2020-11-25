package com.williamspires.bumble.milking.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "grazing")
@Getter
@Setter
public class grazing {

//
    @Id
    @Column(name = "goatid")
    private int goatId;

    @Column(name = "farmerid")
    private String farmerId;
}
