package com.williamspires.bumble.milking.Controllers;

import com.williamspires.bumble.milking.Exceptions.FarmerNotFoundException;
import com.williamspires.bumble.milking.Repositories.*;
import com.williamspires.bumble.milking.models.*;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
public class MilkingController {

    @Autowired
    FarmerRepository farmerRepository;
    @Autowired
    GoatRepository goatRepository;
    @Autowired
    MilkingRepository milkingRepository;
    @Autowired
    MilkingRepositoryInsert milkingRepositoryInsert;
    @Autowired
    MilkExpiryRepositoryInsert milkExpiryRepositoryInsert;
    @Autowired
    MilkExpiryRepository milkExpiryRepository;
    @Autowired
    GrazingRepository grazingRepository;
    @Autowired
    CookingDoesRepository cookingDoesRepository;

    private int counter;

    @GetMapping("/farmer/{id}")
    public Farmer GetFarmerById(@PathVariable(value = "id") String id) throws FarmerNotFoundException {
        return  farmerRepository.findById(id)
                .orElseThrow(() -> new FarmerNotFoundException(id));
    }

    @GetMapping("farmer/{id}/goats")
    public List<Goats> GetListOfGoatsForAFarmer(@PathVariable(value = "id") String id) throws FarmerNotFoundException {
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> new FarmerNotFoundException(id));
        return goatRepository.findGoatsByOwnerId(farmer.getDiscordID());
    }

    @GetMapping("milk/expiry/{id}")
    public List<MilkExpiry> GetMilkExpiryForFarmer(@PathVariable(value = "id") String id) throws FarmerNotFoundException {
        //noinspection unused
        Farmer farmer = farmerRepository.findById(id)
        .orElseThrow(() -> new FarmerNotFoundException(id));

        return milkExpiryRepository.findByDiscordID(id);
    }

    @GetMapping("milk/{id}")
    public MilkingResponse GetMilkingResponseForUser(@PathVariable(value = "id") String id) throws FarmerNotFoundException {
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate today = LocalDate.now();
        LocalDateTime tomorrowMidnight = LocalDateTime.of(today, midnight).plusDays(1);
        LocalDateTime now = LocalDateTime.now();
        long hours = now.until(tomorrowMidnight, ChronoUnit.HOURS);
        now = now.plusHours(hours);
        long minutes = now.until(tomorrowMidnight, ChronoUnit.MINUTES);
        now = now.plusMinutes(minutes);
        long seconds = now.until(tomorrowMidnight, ChronoUnit.SECONDS);
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> new FarmerNotFoundException(id));
        Integer count = milkingRepository.countByDiscordId(id);
        MilkingResponse response = new MilkingResponse();
        if (count > 0) {
            response.setMessage("You have already milked your goats today " +
                    "try again in " + hours + " hours " + minutes + " minutes and " + seconds + " seconds.");
        }
        else {
            List<Goats> goats = goatRepository.findGoatsByOwnerId(id);
            goats = goats.stream()
                    .filter(x -> x.getLevel() >= 100)
                    .collect(Collectors.toList());
            Predicate<Goats> isDazzle = goat -> goat.getBreed().equalsIgnoreCase("Dazzle");
            List<Goats> dazzles = goats.stream()
                    .filter(isDazzle)
                    .collect(Collectors.toList());
            List<Integer> cookingDoes = cookingDoesRepository.findAll().stream()
                    .map(CookingDoes::getGoatid)
                    .collect(Collectors.toList());
            List<Goats> cookingGoats = goats.stream()
                    .filter(goat -> cookingDoes.contains(goat.getId()))
                    .collect(Collectors.toList());
            goats.removeAll(cookingGoats);
            int numberOfGoats = goats.size();
            if (numberOfGoats < 1) {
                response.setMessage("You currently don't have any adult goats that can be milked");
            }
            else {
                List<Integer> grazingGoatIds = grazingRepository.findByFarmerId(farmer.getDiscordID())
                        .stream()
                        .map(grazing::getGoatId)
                        .collect(Collectors.toList());
                List<Goats> boostedBithces = goats.stream()
                        .filter(goat -> grazingGoatIds.contains(goat.getId()))
                        .collect(Collectors.toList());
                goats.removeAll(boostedBithces);
                goats.removeAll(dazzles);
                boostedBithces.removeAll(dazzles);
                double milkAmount = goats.stream().mapToDouble(x -> (x.getLevel() - 99) * 0.3).sum();
                milkAmount += boostedBithces.stream().mapToDouble(x -> ((x.getLevel() - 99) * 0.3) * 1.25).sum();
                int numberOfnaughtyDazzles = 0;
                List<Goats> naughtyDazzles = new ArrayList<>();
                for (Goats dazzle : dazzles) {
                    var randomNumer = ThreadLocalRandom.current().nextInt(1, 3);
                    if (randomNumer == 2) {
                        numberOfnaughtyDazzles++;
                        naughtyDazzles.add(dazzle);
                    }
                }
                dazzles.removeAll(naughtyDazzles);
                milkAmount += dazzles.stream().mapToDouble(x -> ((x.getLevel() - 99) * 0.3) * 1.25).sum();
                goats.addAll(boostedBithces);
                goats.addAll(dazzles);
                DecimalFormat df = new DecimalFormat("#.#");
                Milking milking = new Milking();
                milking.setDiscordId(id);
                milkingRepositoryInsert.insertApiEvent(milking);
                StringBuilder sb = new StringBuilder();
                counter = 0;
                goats.forEach( goat ->  {
                    int startingLevel = goat.getLevel();
                    //noinspection IntegerDivisionInFloatingPointContext
                    goat.setExperience(goat.getExperience() + (goat.getLevel() / 2));
                    goat.setLevel((int) Math.floor((Math.log((goat.getExperience() / 10)) / Math.log(1.05))));
                    goatRepository.save(goat);
                    if (startingLevel != goat.getLevel()) {
                        counter++;
                    }
                });
                int randomNum = ThreadLocalRandom.current().nextInt(1, 15 + 1);
                int numberOfGoatsAffected = 0;
                int levelsLost = 0;
                if (randomNum == 3) {
                    //mastitis
                    int counter = 0;
                    numberOfGoatsAffected = ThreadLocalRandom.current().nextInt(1, numberOfGoats);
                    levelsLost = ThreadLocalRandom.current().nextInt(1, 11);
                    while (counter < numberOfGoatsAffected) {
                        int randomGoat = ThreadLocalRandom.current().nextInt(1, goats.size());
                        var goat = goats.get(randomGoat);
                        int level = goat.getLevel() - levelsLost > 99 ? goat.getLevel() - levelsLost : 100;
                        goat.setLevel(level);
                        goat.setExperience(Math.ceil(10 * Math.pow(1.05, goat.getLevel() -1)));
                        goatRepository.save(goat);
                        goats.remove(goat);
                        counter++;
                    }
                }
                if (farmer.isOats()) {
                    milkAmount = milkAmount * 1.25;
                    farmer.setOats(false);
                }
                farmer.setMilk(farmer.getMilk() + milkAmount);
                farmerRepository.save(farmer);
                MilkExpiry milkExpiry = new MilkExpiry();
                milkExpiry.setDiscordID(id);
                milkExpiry.setMilk(milkAmount);
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, 3);
                String d = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE);
                milkExpiry.setExpirydate(d);
                milkExpiryRepositoryInsert.insertExpiryEvent(milkExpiry);
                if (counter > 0){
                    sb.append(counter == 1 ? counter + " goat has levelled up" : counter + " goats have levelled up.");
                }
                if (randomNum == 3) {
                    sb.append(System.getProperty("line.separator"));
                    sb.append("You discover " + numberOfGoatsAffected + " goats have Mastitis. They've lost up to " +
                            + levelsLost + " levels as they recover.");
                }
                if (numberOfnaughtyDazzles > 0) {
                    sb.append(System.getProperty("line.separator"));
                    sb.append(numberOfnaughtyDazzles + " of your Dazzles were troublesome and " +
                            "therefore were not milked today.");
                }
                response.setMessage("You have successfully milked " + numberOfGoats +
                        " goats and gained " + df.format(milkAmount) + " lbs of milk"
                        + System.getProperty("line.separator")
                        + sb.toString());
            }
        }
        return response;
    }
}
