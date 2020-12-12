package com.williamspires.bumble.milking.Controllers;

import com.williamspires.bumble.milking.Exceptions.FarmerNotFoundException;
import com.williamspires.bumble.milking.Repositories.*;
import com.williamspires.bumble.milking.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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

    @Autowired
    CookingDoesRepository cookingDoesRepository;

    @GetMapping("/daily/{id}")
    public DailyResponse GetDaily(@PathVariable(value = "id") String id) throws FarmerNotFoundException {
        if (dailyRepository.countByDiscordID(id) < 2) {
            Farmer farmer = farmerRepository.findById(id)
                    .orElseThrow(() -> new FarmerNotFoundException(id));
            int baseXp = 25;
            int baseCredits = 50;
            int randomNum = ThreadLocalRandom.current().nextInt(1, 10 + 1);
            farmer.setCredits(farmer.getCredits() + (baseCredits * randomNum));
            farmerRepository.save(farmer);
            List<Goats> goats = goatRepository.findGoatsByOwnerId(id);

            List<Integer> cookingGoatsIds = cookingDoesRepository.findAll().stream()
                    .map(CookingDoes::getGoatid)
                    .collect(Collectors.toList());
            List<Goats> cookingGoats = goats.stream()
                    .filter(goat -> cookingGoatsIds.contains(goat.getId()))
                    .collect(Collectors.toList());
            goats.removeAll(cookingGoats);

            StringBuilder sb = new StringBuilder();
            goats.forEach(goat -> {
                int startingLevel = goat.getLevel();
                goat.setExperience(goat.getExperience() + (baseXp * randomNum));
                goat.setLevel((int) Math.floor((Math.log((goat.getExperience() / 10)) / Math.log(1.05))));
                goatRepository.save(goat);
                if (startingLevel != goat.getLevel()) {
                    sb.append(goat.getName() + " has levelled up to level " + goat.getLevel());
                    sb.append(System.getProperty("line.separator"));
                }
            });
            DailyResponse response = new DailyResponse();
            response.setResponse("You successfully collected your daily and gained " + (baseCredits * randomNum)
                    + " credits and all your goats gained " + (baseXp * randomNum) + " experience."
                    + System.getProperty("line.separator")
                    + sb.toString());
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
