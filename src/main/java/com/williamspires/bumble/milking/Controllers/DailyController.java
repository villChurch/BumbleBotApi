package com.williamspires.bumble.milking.Controllers;

import com.williamspires.bumble.milking.Exceptions.FarmerNotFoundException;
import com.williamspires.bumble.milking.Repositories.*;
import com.williamspires.bumble.milking.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
    @Autowired
    ItemsRepository itemsRepository;

    private int counter;

    @GetMapping("/daily/{id}")
    public DailyResponse GetDaily(@PathVariable(value = "id") String id) throws FarmerNotFoundException {
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate today = LocalDate.now();
        LocalDateTime tomorrowMidnight = LocalDateTime.of(today, midnight).plusDays(1);
        LocalDateTime now = LocalDateTime.now();
        long hours = now.until(tomorrowMidnight, ChronoUnit.HOURS);
        now = now.plusHours(hours);
        long minutes = now.until(tomorrowMidnight, ChronoUnit.MINUTES);
        now = now.plusMinutes(minutes);
        long seconds = now.until(tomorrowMidnight, ChronoUnit.SECONDS);
        if (dailyRepository.countByDiscordID(id) < 1) {
            Farmer farmer = farmerRepository.findById(id)
                    .orElseThrow(() -> new FarmerNotFoundException(id));
            int baseXp = 50;
            int randomNum = ThreadLocalRandom.current().nextInt(1, 10 + 1);
            List<Goats> goats = goatRepository.findGoatsByOwnerId(id);
            int runningCosts = goats.size() * 10;
            if (runningCosts > farmer.getCredits()) {
                DailyResponse response = new DailyResponse();
                response.setResponse("Your current balance of " + farmer.getCredits() + " is less than the " +
                        "expenditure for feed and supplies of "
                        + runningCosts + " credits, therefore daily cannot be run.");
                return response;
            }
            Items alfalfa = itemsRepository.findByOwnerIdAndName(id, "alfalfa");
            farmer.setCredits(farmer.getCredits() - runningCosts);
            farmerRepository.save(farmer);

            List<Integer> cookingGoatsIds = cookingDoesRepository.findAll().stream()
                    .map(CookingDoes::getGoatid)
                    .collect(Collectors.toList());
            List<Goats> cookingGoats = goats.stream()
                    .filter(goat -> cookingGoatsIds.contains(goat.getId()))
                    .collect(Collectors.toList());
            goats.removeAll(cookingGoats);

            StringBuilder sb = new StringBuilder();
            counter = 0;
            if (alfalfa != null && alfalfa.getAmount() > 0) {
                itemsRepository.delete(alfalfa);
                baseXp = (int) Math.ceil(baseXp * 1.25);
            }
            int finalBaseXp = baseXp;
            goats.forEach(goat -> {
                int startingLevel = goat.getLevel();
                goat.setExperience(goat.getExperience() + (finalBaseXp * randomNum));
                goat.setLevel((int) Math.floor((Math.log((goat.getExperience() / 10)) / Math.log(1.05))));
                goatRepository.save(goat);
                if (startingLevel != goat.getLevel()) {
                    incrementCounter();
                }
            });
            if (counter > 0) {
                sb.append(counter == 1 ? counter + " goat has levelled up" : counter + " goats have levelled up.");
            }
            DailyResponse response = new DailyResponse();
            response.setResponse("You completed your daily chores and all your goats gained " + (baseXp * randomNum) + " experience."
                    + System.getProperty("line.separator")
                    + sb.toString()
                    + System.getProperty("line.separator")
                    + "Your expenditure for feed and supplies today is " + runningCosts + " credits.");
            Daily daily = new Daily();
            daily.setDiscordID(id);
            dailyRepositoryInsert.insertApiEvent(daily);
            return response;
        }
        else  {
            DailyResponse response = new DailyResponse();
            response.setResponse("You have already completed your chores today. Chores need doing again in "
            + hours + " hours " + minutes + " minutes and " + seconds + " seconds.");
            return response;
        }
    }

    private void incrementCounter() {
        counter++;
    }
}
