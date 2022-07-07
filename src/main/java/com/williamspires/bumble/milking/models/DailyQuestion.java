package com.williamspires.bumble.milking.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Table(name="DailyQuestion")
public class DailyQuestion {

    @Column(name = "id")
    @GeneratedValue
    @Id
    private int id;

    @Column(name = "question")
    private String question;
}
