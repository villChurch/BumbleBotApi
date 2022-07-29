package com.williamspires.bumble.milking.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Table(name="dailyquestion")
@Entity
public class DailyQuestion {

    @Column(name = "id")
    @GeneratedValue
    @Id
    private int id;

    @Column(name = "question")
    private String question;
}
