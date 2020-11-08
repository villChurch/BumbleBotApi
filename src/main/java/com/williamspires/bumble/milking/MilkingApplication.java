package com.williamspires.bumble.milking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MilkingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MilkingApplication.class, args);
    }

}
