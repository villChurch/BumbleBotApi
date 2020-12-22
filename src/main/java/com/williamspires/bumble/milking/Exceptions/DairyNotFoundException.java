package com.williamspires.bumble.milking.Exceptions;

public class DairyNotFoundException extends Exception {

    private String discordId;

    public DairyNotFoundException(String discordId) {
        super(String.format("Dairy not found for member with id: '%s'", discordId));
    }
}
