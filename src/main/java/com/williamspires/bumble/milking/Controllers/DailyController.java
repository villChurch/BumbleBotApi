package com.williamspires.bumble.milking.Controllers;

import com.williamspires.bumble.milking.Exceptions.FarmerNotFoundException;
import com.williamspires.bumble.milking.Repositories.*;
import com.williamspires.bumble.milking.Webhook.PostMilkExpiry;
import com.williamspires.bumble.milking.models.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@RestController
public class DailyController {

    final
    DailyRepository dailyRepository;

    final
    FarmerRepository farmerRepository;

    final
    GoatRepository goatRepository;

    final
    DailyRepositoryInsert dailyRepositoryInsert;

    final
    CookingDoesRepository cookingDoesRepository;
    final
    ItemsRepository itemsRepository;

    final
    MaintenanceRepository maintenanceRepository;

    final
    MaintenanceRepositoryInsert maintenanceRepositoryInsert;

    private int counter;

    public DailyController(DailyRepository dailyRepository, FarmerRepository farmerRepository, GoatRepository goatRepository, DailyRepositoryInsert dailyRepositoryInsert, CookingDoesRepository cookingDoesRepository, ItemsRepository itemsRepository, MaintenanceRepository maintenanceRepository, MaintenanceRepositoryInsert maintenanceRepositoryInsert) {
        this.dailyRepository = dailyRepository;
        this.farmerRepository = farmerRepository;
        this.goatRepository = goatRepository;
        this.dailyRepositoryInsert = dailyRepositoryInsert;
        this.cookingDoesRepository = cookingDoesRepository;
        this.itemsRepository = itemsRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.maintenanceRepositoryInsert = maintenanceRepositoryInsert;
    }

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

            int numberOfGoats = goats.size();
            int maintenanceChanceStacker = numberOfGoats >= 150 ? 10 : (int) (Math.ceil(1500 / numberOfGoats));
            boolean maintenance = ThreadLocalRandom.current().nextInt(0, maintenanceChanceStacker + 1) == 3;

            List<Integer> cookingGoatsIds = cookingDoesRepository.findAll().stream()
                    .map(CookingDoes::getGoatid)
                    .collect(Collectors.toList());
            List<Goats> cookingGoats = goats.stream()
                    .filter(goat -> cookingGoatsIds.contains(goat.getId()))
                    .collect(Collectors.toList());
            goats.removeAll(cookingGoats);

            StringBuilder sb = new StringBuilder();
            if (maintenance) {
                sb.append(maintenance(farmer));
                sb.append(System.getProperty("line.separator"));
            }
            counter = 0;
            if (alfalfa != null && alfalfa.getAmount() > 0) {
                itemsRepository.delete(alfalfa);
                baseXp = (int) Math.ceil(baseXp * 1.25);
            }

            Optional<Maintenance> farmersMaintenance = maintenanceRepository.findMaintenanceByFarmerid(farmer.getDiscordID());

            double maintenanceMultiplier = 1;
            if (farmersMaintenance.isPresent()) {
                maintenanceMultiplier = farmersMaintenance.get().isDailyBoost() ? 1.10 : maintenanceMultiplier;
                farmersMaintenance.get().setDailyBoost(false);
                maintenanceRepository.save(farmersMaintenance.get());
            }

            int finalBaseXp = (int) Math.ceil(baseXp * maintenanceMultiplier);
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
                    + sb
                    + System.getProperty("line.separator")
                    + "Your expenditure for feed and supplies today is " + runningCosts + " credits.");
            Daily daily = new Daily();
            daily.setDiscordID(id);
            dailyRepositoryInsert.insertApiEvent(daily);
            PostMilkExpiry.SendWebhook(response.toString());
            return response;
        }
        else  {
            DailyResponse response = new DailyResponse();
            response.setResponse("You have already completed your chores today. Chores need doing again in "
            + hours + " hours " + minutes + " minutes and " + seconds + " seconds.");
            return response;
        }
    }

    private Maintenance returnFarmersMaintenance(Farmer farmer) {
        Optional<Maintenance> farmersMaintenance = maintenanceRepository.findMaintenanceByFarmerid(farmer.getDiscordID());
        if (!farmersMaintenance.isPresent()) {
            Maintenance newFarmersMaintenance = new Maintenance();
            newFarmersMaintenance.setFarmerid(farmer.getDiscordID());
            maintenanceRepositoryInsert.insertMaintenanceWithEntityManager(newFarmersMaintenance);
            return maintenanceRepository.findMaintenanceByFarmerid(farmer.getDiscordID()).get();
        }
        else {
            return farmersMaintenance.get();
        }
    }

    private String maintenance(Farmer farmer) {
        Maintenance farmersMaintenance = returnFarmersMaintenance(farmer);
        if (farmersMaintenance.isNeedsMaintenance()) {
            return "";
        }
        boolean goodOutcome = ThreadLocalRandom.current().nextInt(1, 11) % 2 == 0;
        if (goodOutcome) {
            int scenario = ThreadLocalRandom.current().nextInt(0, 3);
            if (scenario == 0) {
                farmersMaintenance.setDailyBoost(true);
                maintenanceRepository.save(farmersMaintenance);
                return "Due to good maintenance your goats have gained an additional 10% xp boost.";
            } else if (scenario == 1) {
                farmersMaintenance.setMilkingBoost(true);
                maintenanceRepository.save(farmersMaintenance);
                return "Due to good maintenance your goats have gained an additional 10% milk boost for today";
            }
            farmersMaintenance.setDailyBoost(true);
            farmersMaintenance.setMilkingBoost(true);
            maintenanceRepository.save(farmersMaintenance);
            return "Due to exceptional maintenance your goats have gained a 10% xp boost and a 10% milk boost for today's tasks";
        }
        else {
            farmersMaintenance.setNeedsMaintenance(true);
            farmersMaintenance.setDailyBoost(false);
            farmersMaintenance.setMilkingBoost(false);
            maintenanceRepository.save(farmersMaintenance);
            return "Unfortunately your equipment requires some maintenance and as a result your goats milk production will drop until it is fixed.";
        }
    }

    private void incrementCounter() {
        counter++;
    }
}
