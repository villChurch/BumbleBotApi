package com.williamspires.bumble.milking.Controllers;

import com.williamspires.bumble.milking.Exceptions.FarmerNotFoundException;
import com.williamspires.bumble.milking.Repositories.FarmerRepository;
import com.williamspires.bumble.milking.Repositories.WorkingRepository;
import com.williamspires.bumble.milking.models.Farmer;
import com.williamspires.bumble.milking.models.Working;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RestController
public class WorkingController {

    @Autowired
    WorkingRepository workingRepository;

    @Autowired
    FarmerRepository farmerRepository;

    @GetMapping("/work/stop/{id}")
    public String StopWorking(@PathVariable(value = "id") String id) throws FarmerNotFoundException {
        Farmer farmer = farmerRepository.findById(id).orElseThrow(() -> new FarmerNotFoundException(id));
        Working working = workingRepository.findFirstByFarmerid(farmer.getDiscordID());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = working.getStarttime().toLocalDateTime();
        long hours = startTime.until(now, ChronoUnit.HOURS);
        startTime = startTime.plusHours(hours);
        long minutes = startTime.until(now, ChronoUnit.MINUTES);
        startTime = startTime.plusMinutes(minutes);
        long seconds = startTime.until(now, ChronoUnit.SECONDS);
        workingRepository.delete(working);
        return "You worked for " + hours + " hours " + minutes + " minutes and " + seconds + " seconds.";
    }
}
