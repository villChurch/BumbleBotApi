package com.williamspires.bumble.milking.Controllers;

import com.williamspires.bumble.milking.Exceptions.FarmerNotFoundException;
import com.williamspires.bumble.milking.Repositories.*;
import com.williamspires.bumble.milking.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        Farmer farmer = farmerRepository.findById(id)
        .orElseThrow(() -> new FarmerNotFoundException(id));

        return milkExpiryRepository.findByDiscordID(id);
    }

    @GetMapping("milk/{id}")
    public MilkingResponse GetMilkingResponseForUser(@PathVariable(value = "id") String id) throws FarmerNotFoundException, ParseException {
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> new FarmerNotFoundException(id));
        Integer count = milkingRepository.countByDiscordId(id);
        MilkingResponse response = new MilkingResponse();
        if (count > 0) {
            response.setMessage("You have already milked your goats today " +
                    "try again tomorrow when they have more milk");
        }
        else {
            List<Goats> goats = goatRepository.findGoatsByOwnerId(id);
            goats = goats.stream()
                    .filter(x -> x.getLevel() >= 100)
                    .collect(Collectors.toList());
            if (goats.size() < 1) {
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
                double milkAmount = goats.stream().mapToDouble(x -> (x.getLevel() - 99) * 0.3).sum();
                milkAmount += boostedBithces.stream().mapToDouble(x -> ((x.getLevel() - 99) * 0.3) * 1.25).sum();
                goats.addAll(boostedBithces);
                DecimalFormat df = new DecimalFormat("#.#");
                Milking milking = new Milking();
                milking.setDiscordId(id);
                milkingRepositoryInsert.insertApiEvent(milking);
                StringBuilder sb = new StringBuilder();
                goats.forEach( goat ->  {
                    int startingLevel = goat.getLevel();
                    goat.setExperience(goat.getExperience() + (goat.getLevel() / 2));
                    goat.setLevel((int) Math.floor((Math.log((goat.getExperience() / 10)) / Math.log(1.05))));
                    goatRepository.save(goat);
                    if (startingLevel != goat.getLevel()) {
                        sb.append(goat.getName() + " has levelled up to level " + goat.getLevel());
                        sb.append(System.getProperty("line.separator"));
                    }
                });
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
                response.setMessage("You have successfully milked " + goats.size() +
                        " goats and gained " + df.format(milkAmount) + " lbs of milk"
                        + System.getProperty("line.separator")
                        + sb.toString());
            }
        }
        return response;
    }
}
