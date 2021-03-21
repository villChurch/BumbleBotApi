package com.williamspires.bumble.milking.Exceptions;

public class DairyNotFoundException extends Exception {

    public DairyNotFoundException(String discordId) {
        super(String.format("Dairy not found for member with id: '%s'", discordId));
    }
}
