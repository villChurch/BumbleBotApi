package com.williamspires.bumble.milking.Controllers;

import com.williamspires.bumble.milking.Exceptions.CaveNotFoundException;
import com.williamspires.bumble.milking.Exceptions.DairyNotFoundException;
import com.williamspires.bumble.milking.Exceptions.FarmerNotFoundException;
import com.williamspires.bumble.milking.Repositories.*;
import com.williamspires.bumble.milking.models.*;
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
    @Autowired
    SoftCheeseExpiryRepository softCheeseExpiryRepository;
    @Autowired
    CaveRepository caveRepository;
    @Autowired
    AgingInsertRepository agingInsertRepository;

    @GetMapping("dairy/cave/{id}/add/softcheese/{amount}")
    public String AddSoftCheeseToCaveByFarmerId(@PathVariable(value = "id") String id,
                                                @PathVariable(value = "amount") Integer amount) throws FarmerNotFoundException, DairyNotFoundException, CaveNotFoundException {
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> new FarmerNotFoundException(id));
        Dairy dairy = dairyRepository.findByOwnerId(id)
                .orElseThrow(() -> new DairyNotFoundException(id));
        Cave cave = caveRepository.findById(id)
                .orElseThrow(() -> new CaveNotFoundException(id));
        double softCheese = dairy.getSoftcheese();
        if (amount > softCheese) {
            return "You cannot add more soft cheese than you own to your cave";
        }
        List<SoftCheeseExpiry> farmersSoftCheese = softCheeseExpiryRepository.findAllByDiscordID(id);
        int amountToAdd = amount;
        while(amountToAdd > 0) {
            SoftCheeseExpiry softCheeseExpiry = farmersSoftCheese.get(0);
            amountToAdd = amountToAdd - softCheeseExpiry.getAmount();
            farmersSoftCheese.remove(0);
            if (softCheeseExpiry.getAmount() <= 0) {
                softCheeseExpiryRepository.delete(softCheeseExpiry);
            }
            else  {
                softCheeseExpiryRepository.save(softCheeseExpiry);
            }
        }
        cave.setSoftcheese(cave.getSoftcheese() + amount);
        caveRepository.save(cave);
        dairy.setSoftcheese(dairy.getSoftcheese() - amount);
        dairyRepository.save(dairy);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);
        String d = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE);
        Aging aging = new Aging();
        aging.setDiscordId(id);
        aging.setAmount(amount);
        aging.setExpirydate(d);
        agingInsertRepository.insertApiEvent(aging);
        return  amount + " lbs of soft cheese have been added to your cave and will be processed into hard cheese in 1-2 days";
    }
    
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
                amountToAdd = 0;
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
