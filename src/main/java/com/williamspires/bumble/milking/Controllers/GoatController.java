package com.williamspires.bumble.milking.Controllers;

import com.williamspires.bumble.milking.Repositories.FarmerRepository;
import com.williamspires.bumble.milking.Repositories.GoatRepository;
import com.williamspires.bumble.milking.models.Goats;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.function.Predicate;

@RestController
public class GoatController {

    @Autowired
    FarmerRepository farmerRepository;

    @Autowired
    GoatRepository goatRepository;

    @GetMapping("goat/herd/rename/{id}/{name}")
    public String PrefixGoatsWithFarmName(@PathVariable("id") String id, @PathVariable("name") String name) {
        var farmersGoats = goatRepository.findGoatsByOwnerId(id);
        if (farmersGoats.size() == 0) {
            return "You don't own any goats.";
        }
        Predicate<Goats> matchesPrefix = goats -> goats.getName().toLowerCase().startsWith(name.toLowerCase());
        farmersGoats.removeIf(matchesPrefix);
        if (farmersGoats.size() > 0) {
            farmersGoats.forEach(goat -> {
                goat.setName(name + " " + goat.getName());
                goatRepository.save(goat);
            });
        }
        String response = farmersGoats.size() > 0 ? "Successfully renamed " + farmersGoats.size() + " goats."
                : "All goats matched the prefix.";
        return response;
    }

    @GetMapping("goat/herd/prefix/remove/{id}/{prefix}")
    public String RemovePrefixFromGoats(@PathVariable("id") String id, @PathVariable("prefix") String prefix) {
        var farmersGoats = goatRepository.findGoatsByOwnerId(id);
        if (farmersGoats.size() == 0) {
            return "You don't own any goats";
        }
        Predicate<Goats> doesNotHavePrefix = goats -> !goats.getName().toLowerCase().startsWith(prefix.toLowerCase());
        farmersGoats.removeIf(doesNotHavePrefix);
        if (farmersGoats.size() == 0) {
            return "None of your goats have this herd name at the start.";
        }
        farmersGoats.forEach(goat -> {
            goat.setName(goat.getName().replace(prefix, "").trim());
            goatRepository.save(goat);
        });
        return "Removed herd name from " + farmersGoats.size() + " goats.";
    }
}
