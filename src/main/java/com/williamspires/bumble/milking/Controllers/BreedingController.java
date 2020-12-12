package com.williamspires.bumble.milking.Controllers;

import com.williamspires.bumble.milking.Exceptions.FarmerNotFoundException;
import com.williamspires.bumble.milking.Repositories.*;
import com.williamspires.bumble.milking.models.CookingDoes;
import com.williamspires.bumble.milking.models.Farmer;
import com.williamspires.bumble.milking.models.Goats;
import com.williamspires.bumble.milking.models.KiddingPen;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class BreedingController {

    @Autowired
    CookingDoesInsert cookingDoesInsert;

    @Autowired
    CookingDoesRepository cookingDoesRepository;

    @Autowired
    KiddingPenRepository kiddingPenRepository;

    @Autowired
    GoatRepository goatRepository;

    @Autowired
    FarmerRepository farmerRepository;

    @GetMapping("/breeding/{id}/{goatId}")
    public String AddGoatToKiddingPen(@PathVariable(value = "id") String id,
                                      @PathVariable(value = "goatId") int goatId) throws FarmerNotFoundException {
        List<KiddingPen> kiddingPens = kiddingPenRepository.findByOwnerId(id);
        var kiddingPen = kiddingPens.get(0);
        int capacity = kiddingPen.getCapacity();
        List<Goats> playersGoats = goatRepository.findGoatsByOwnerId(id);
        List<CookingDoes> playersCookingDoes = cookingDoesRepository.findAll().stream()
                .filter(cookingDoes1 -> playersGoats.stream()
                        .anyMatch(goats -> goats.getId() == cookingDoes1.getGoatid()))
                .collect(Collectors.toList());
        int numberCooking = playersCookingDoes.size();
        if (numberCooking >= capacity) {
            return "You can't send another goat for breeding as your shelter is currently full";
        }
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> new FarmerNotFoundException(id));
        int credits = farmer.getCredits();
        if (credits < 200) {
            return "You need 200 credits to pay the stud fee";
        }
        else {
            farmer.setCredits(credits - 200);
            farmerRepository.save(farmer);
        }
        CookingDoes cookingDoe = new CookingDoes();
        cookingDoe.setGoatid(goatId);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 3);
        String d = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE);
        cookingDoe.setDueDate(d);
        cookingDoesInsert.startCooking(cookingDoe);
        return playersGoats.stream()
        .filter(goats -> goats.getId() == goatId).findFirst().get().getName() + " has been moved to the shelter " +
                "and will be ready after " + d;
    }
}
