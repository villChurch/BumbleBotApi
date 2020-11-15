package com.williamspires.bumble.milking.Controllers;

import com.williamspires.bumble.milking.Exceptions.FarmerNotFoundException;
import com.williamspires.bumble.milking.Repositories.DailyRepository;
import com.williamspires.bumble.milking.Repositories.DailyRepositoryInsert;
import com.williamspires.bumble.milking.Repositories.FarmerRepository;
import com.williamspires.bumble.milking.Repositories.GoatRepository;
import com.williamspires.bumble.milking.models.Daily;
import com.williamspires.bumble.milking.models.DailyResponse;
import com.williamspires.bumble.milking.models.Farmer;
import com.williamspires.bumble.milking.models.Goats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class DailyController {

    @Autowired
    DailyRepository dailyRepository;

    @Autowired
    FarmerRepository farmerRepository;

    @Autowired
    GoatRepository goatRepository;

    @Autowired
    DailyRepositoryInsert dailyRepositoryInsert;

    @GetMapping("/daily/{id}")
    public DailyResponse GetDaily(@PathVariable(value = "id") String id) throws FarmerNotFoundException {
        if (dailyRepository.countByDiscordID(id) < 1) {
            Farmer farmer = farmerRepository.findById(id)
                    .orElseThrow(() -> new FarmerNotFoundException(id));
            int baseXp = 50;
            int baseCredits = 100;
            int randomNum = ThreadLocalRandom.current().nextInt(1, 10 + 1);
            farmer.setCredits(farmer.getCredits() + (baseCredits * randomNum));
            farmerRepository.save(farmer);
            List<Goats> goats = goatRepository.findGoatsByOwnerId(id);
            goats.forEach(goat -> {
                goat.setExperience(goat.getExperience() + (baseXp * randomNum));
                goat.setLevel((int) Math.floor((Math.log((goat.getExperience() / 10)) / Math.log(1.05))));
                goatRepository.save(goat);
            });
            DailyResponse response = new DailyResponse();
            response.setResponse("You successfully collected your daily and gained " + (baseCredits * randomNum)
                    + " credits and all your goats gained " + (baseXp * randomNum) + " experience.");
            Daily daily = new Daily();
            daily.setDiscordID(id);
            dailyRepositoryInsert.insertApiEvent(daily);
            return response;
        }
        else  {
            DailyResponse response = new DailyResponse();
            response.setResponse("You have already collected your daily today try again tomorrow. Daily resets at midnight GB time.");
            return response;
        }
    }
}
