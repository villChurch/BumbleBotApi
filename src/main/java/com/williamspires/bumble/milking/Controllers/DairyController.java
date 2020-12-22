package com.williamspires.bumble.milking.Controllers;

import com.williamspires.bumble.milking.Exceptions.DairyNotFoundException;
import com.williamspires.bumble.milking.Exceptions.FarmerNotFoundException;
import com.williamspires.bumble.milking.Repositories.CurdingInsertRepository;
import com.williamspires.bumble.milking.Repositories.DairyRepository;
import com.williamspires.bumble.milking.Repositories.FarmerRepository;
import com.williamspires.bumble.milking.Repositories.MilkExpiryRepository;
import com.williamspires.bumble.milking.models.Curding;
import com.williamspires.bumble.milking.models.Dairy;
import com.williamspires.bumble.milking.models.Farmer;
import com.williamspires.bumble.milking.models.MilkExpiry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@RestController
public class DairyController {

    @Autowired
    DairyRepository dairyRepository;
    @Autowired
    FarmerRepository farmerRepository;
    @Autowired
    MilkExpiryRepository milkExpiryRepository;
    @Autowired
    CurdingInsertRepository curdingInsertRepository;

    @GetMapping("dairy/{id}/add/milk/{amount}")
    public String AddMilkToDairyByFarmerId(@PathVariable(value = "id") String id,
                                           @PathVariable(value = "amount") Integer amount) throws FarmerNotFoundException, DairyNotFoundException {
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> new FarmerNotFoundException(id));
        Dairy dairy = dairyRepository.findByOwnerId(id)
                .orElseThrow(() -> new DairyNotFoundException(id));
        double milk = farmer.getMilk();
        if (amount > milk) {
            return "You cannot add more milk than you have to the dairy";
        }
        List<MilkExpiry> farmersMilk = milkExpiryRepository.findByDiscordID(id);
        int amountToAdd = amount;
        farmer.setMilk(farmer.getMilk() - amount);
        farmerRepository.save(farmer);
        while(amountToAdd > 0 || farmersMilk.size() < 1) {
            MilkExpiry fm = farmersMilk.get(0);
            if (amountToAdd > fm.getMilk()) {
                amountToAdd -= fm.getMilk();
                milkExpiryRepository.delete(fm);
                farmersMilk.remove(0);
            } else {
                fm.setMilk(fm.getMilk() - amountToAdd);
                milkExpiryRepository.save(fm);
                amountToAdd -= amountToAdd;
            }
        }
        dairy.setMilk(dairy.getMilk() + amount);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);
        String d = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE);
        Curding curding = new Curding();
        curding.setAmount(amount);
        curding.setDiscordId(id);
        curding.setExpirydate(d);
        curdingInsertRepository.insertWithEntityManager(curding);
        return  amount + " lbs of milk has been added to your dairy and will be processed into soft cheese in 1-2 days";
    }

}
