package com.williamspires.bumble.milking.Exceptions;

public class CaveNotFoundException extends Exception {

    public CaveNotFoundException(String discordId) {
        super(String.format("Cave not found for member with id: '%s'", discordId));
    }
}
