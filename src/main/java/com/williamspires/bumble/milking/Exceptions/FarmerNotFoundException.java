package com.williamspires.bumble.milking.Exceptions;

public class FarmerNotFoundException extends Exception {

    private String discordId;

    public FarmerNotFoundException(String discordId) {
        super(String.format("Farmer not found for member with id: '%s'", discordId));
    }

}
