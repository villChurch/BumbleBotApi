package com.williamspires.bumble.milking.ScheduledTasks;

import com.williamspires.bumble.milking.Repositories.FarmerRepository;
import com.williamspires.bumble.milking.Repositories.GoatRepository;
import com.williamspires.bumble.milking.Repositories.GrazingRepository;
import com.williamspires.bumble.milking.models.Goats;
import com.williamspires.bumble.milking.models.grazing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GrazingTask {

    @Autowired
    GrazingRepository grazingRepository;
    @Autowired
    FarmerRepository farmerRepository;
    @Autowired
    GoatRepository goatRepository;

    @Scheduled(cron = "5 0 */12 * * *", zone = "GMT")
    public void GrazingTask() {
        log.info("Start grazing task");
        List<Integer> grazingGoatsIds = grazingRepository.findAll()
                .stream()
                .map((grazing::getGoatId))
                .collect(Collectors.toList());
        List<Goats> allGrazingGoats = goatRepository.findAll()
                .stream()
                .filter(goat -> grazingGoatsIds.contains(goat.getId()))
                .collect(Collectors.toList());
        allGrazingGoats.forEach(goat -> {
            double exp = goat.getExperience();
            exp = exp + (goat.getLevel() * 0.25);
            goat.setExperience(exp);
            goat.setLevel((int) Math.floor((Math.log((goat.getExperience() / 10)) / Math.log(1.05))));
            goatRepository.save(goat);
        });
    }
}
