package com.williamspires.bumble.milking.Controllers;

import com.williamspires.bumble.milking.Exceptions.FarmerNotFoundException;
import com.williamspires.bumble.milking.Repositories.FarmerPerksRepository;
import com.williamspires.bumble.milking.Repositories.FarmerRepository;
import com.williamspires.bumble.milking.Repositories.PerkRepository;
import com.williamspires.bumble.milking.Repositories.WorkingRepository;
import com.williamspires.bumble.milking.models.Farmer;
import com.williamspires.bumble.milking.models.FarmerPerks;
import com.williamspires.bumble.milking.models.Perks;
import com.williamspires.bumble.milking.models.Working;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class WorkingController {

    @Autowired
    WorkingRepository workingRepository;

    @Autowired
    FarmerRepository farmerRepository;
    @Autowired
    PerkRepository perkRepository;
    @Autowired
    FarmerPerksRepository farmerPerksRepository;

    @GetMapping("/work/stop/{id}")
    public String StopWorking(@PathVariable(value = "id") String id) throws FarmerNotFoundException {
        Farmer farmer = farmerRepository.findById(id).orElseThrow(() -> new FarmerNotFoundException(id));
        Working working = workingRepository.findFirstByFarmerid(farmer.getDiscordID());
        List<FarmerPerks> farmersPerks = farmerPerksRepository.findFarmerPerksByFarmerid(farmer.getDiscordID());
        List<Perks> allPerks = perkRepository.findAll();
        List<Perks> farmersPurchasedPerks = allPerks.stream().filter(perk ->
                farmersPerks.stream().map(FarmerPerks::getPerkid).collect(Collectors.toList())
                        .contains(perk.getId())).collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = working.getStarttime().toLocalDateTime();
        long hours = startTime.until(now, ChronoUnit.HOURS);
        startTime = startTime.plusHours(hours);
        long minutes = startTime.until(now, ChronoUnit.MINUTES);
        startTime = startTime.plusMinutes(minutes);
        long seconds = startTime.until(now, ChronoUnit.SECONDS);
        log.info("Seconds {} minutes {} hours {}", seconds, minutes, hours);
        workingRepository.delete(working);
        double xpToAdd = (double) hours * 2;
        log.info("xp after hours {}", xpToAdd);
        xpToAdd += (double) (minutes/60) * 2;
        log.info("xp after minutes {}", xpToAdd);
        xpToAdd += (double) (seconds/60/60) * 2;
        log.info("xp after seconds {}", xpToAdd);
        if (farmersPurchasedPerks.stream().map(Perks::getId).collect(Collectors.toList()).contains(2)) {
            xpToAdd = xpToAdd * 2;
            log.info(   "XP has been doubled due to perks");
        }
        int startLevel = farmer.getLevel();
        if (xpToAdd > 0) {
            farmer.setExperience(farmer.getExperience() + xpToAdd);
            farmer.setLevel((int) Math.floor((Math.log((farmer.getExperience() / 50)) / Math.log(1.4))));
            if (startLevel < farmer.getLevel() && farmer.getLevel() < 16) {
                farmer.setPerkpoints(farmer.getPerkpoints() + 1);
            }
            if (farmer.getLevel() < 0) {
                farmer.setLevel(0);
            }
            farmerRepository.save(farmer);
        } else if (xpToAdd < 0) {
            xpToAdd = 0;
        }
        return farmer.getLevel() == startLevel ?
                "You worked for " + hours + " hours " + minutes + " minutes and " + seconds + " seconds and gained " + xpToAdd + " xp." :
                "You worked for " + hours + " hours " + minutes + " minutes and " + seconds +
                        " seconds and gained " + xpToAdd + " xp and are now level " + farmer.getLevel() + ".";
    }
}
