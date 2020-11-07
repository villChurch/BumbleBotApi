package com.williamspires.bumble.milking.Controllers;

import com.williamspires.bumble.milking.Exceptions.FarmerNotFoundException;
import com.williamspires.bumble.milking.Repositories.FarmerRepository;
import com.williamspires.bumble.milking.Repositories.GoatRepository;
import com.williamspires.bumble.milking.Repositories.MilkingRepository;
import com.williamspires.bumble.milking.Repositories.MilkingRepositoryInsert;
import com.williamspires.bumble.milking.models.Farmer;
import com.williamspires.bumble.milking.models.Goats;
import com.williamspires.bumble.milking.models.Milking;
import com.williamspires.bumble.milking.models.MilkingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.util.List;
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

    @GetMapping("milk/{id}")
    public MilkingResponse GetMilkingResponseForUser(@PathVariable(value = "id") String id) throws FarmerNotFoundException {
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
                double milkAmount = goats.stream().mapToDouble(x -> (x.getLevel() - 100) * 0.3).sum();
                DecimalFormat df = new DecimalFormat("#.#");
                response.setMessage("You have successfully milked " + goats.size() +
                                " goats and gained " + df.format(milkAmount) + " lbs of milk");
                Milking milking = new Milking();
                milking.setDiscordId(id);
                milkingRepositoryInsert.insertApiEvent(milking);
                goats.forEach( goat ->  {
                    goat.setExperience(goat.getExperience() + (goat.getLevel() / 2));
                    goat.setLevel((int) Math.floor((Math.log((goat.getExperience() / 10)) / Math.log(1.05))));
                    goatRepository.save(goat);
                });
                farmer.setMilk(farmer.getMilk() + milkAmount);
                farmerRepository.save(farmer);
            }
        }
        return response;
    }
}
