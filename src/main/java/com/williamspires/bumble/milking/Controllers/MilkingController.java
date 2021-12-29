package com.williamspires.bumble.milking.Controllers;

import com.williamspires.bumble.milking.Exceptions.FarmerNotFoundException;
import com.williamspires.bumble.milking.Repositories.*;
import com.williamspires.bumble.milking.Webhook.PostMilkExpiry;
import com.williamspires.bumble.milking.models.*;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class MilkingController {

    final FarmerRepository farmerRepository;
    final GoatRepository goatRepository;
    final MilkingRepository milkingRepository;
    final MilkingRepositoryInsert milkingRepositoryInsert;
    final GrazingRepository grazingRepository;
    final CookingDoesRepository cookingDoesRepository;
    final MaintenanceRepository maintenanceRepository;
    final FarmerPerksRepository farmerPerksRepository;
    final PerkRepository perkRepository;

    private int counter;

    public MilkingController(FarmerRepository farmerRepository, GoatRepository goatRepository, MilkingRepository milkingRepository, MilkingRepositoryInsert milkingRepositoryInsert, GrazingRepository grazingRepository, CookingDoesRepository cookingDoesRepository, MaintenanceRepository maintenanceRepository, FarmerPerksRepository farmerPerksRepository, PerkRepository perkRepository) {
        this.farmerRepository = farmerRepository;
        this.goatRepository = goatRepository;
        this.milkingRepository = milkingRepository;
        this.milkingRepositoryInsert = milkingRepositoryInsert;
        this.grazingRepository = grazingRepository;
        this.cookingDoesRepository = cookingDoesRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.farmerPerksRepository = farmerPerksRepository;
        this.perkRepository = perkRepository;
    }

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
        List<FarmerPerks> farmersPerks = farmerPerksRepository.findFarmerPerksByFarmerid(farmer.getDiscordID());
        List<Perks> allPerks = perkRepository.findAll();
        List<Perks> farmersPurchasedPerks = allPerks.stream().filter(perk ->
                farmersPerks.stream().map(FarmerPerks::getPerkid).collect(Collectors.toList())
                        .contains(perk.getId())).collect(Collectors.toList());
        double perkMultiplier = 1.00;
        if (farmersPurchasedPerks.stream().map(Perks::getId).collect(Collectors.toList()).contains(13)) {
            perkMultiplier = 1.15;
        }
        else if (farmersPurchasedPerks.stream().map(Perks::getId).collect(Collectors.toList()).contains(8)) {
            perkMultiplier = 1.10;
        }
        else if (farmersPurchasedPerks.stream().map(Perks::getId).collect(Collectors.toList()).contains(6)) {
            perkMultiplier = 1.05;
        }
        Integer count = milkingRepository.countByDiscordId(id);
        MilkingResponse response = new MilkingResponse();
        if (count > 0) {
            response.setMessage("You have already milked your goats today. " +
                    "Try again tomorrow when they have more milk");
        }
        else {
            List<Goats> goats = goatRepository.findGoatsByOwnerId(id);
            goats = goats.stream()
                    .filter(x -> x.getLevel() >= 100)
                    .filter(x -> !x.getBreed().equalsIgnoreCase("Buck"))
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
                List<Goats> boostedBitches = goats.stream()
                        .filter(goat -> grazingGoatIds.contains(goat.getId()))
                        .collect(Collectors.toList());
                goats.removeAll(boostedBitches);
                goats.removeAll(dazzles);
                boostedBitches.removeAll(dazzles);
                double milkAmount = goats.stream().mapToDouble(x -> (x.getLevel() - 99) * 0.3).sum();
                milkAmount += boostedBitches.stream().mapToDouble(x -> ((x.getLevel() - 99) * 0.3) * 1.25).sum();
                int numberOfnaughtyDazzles = 0;
                List<Goats> naughtyDazzles = new ArrayList<>();
                for (Goats dazzle : dazzles) {
                    var randomNumer = ThreadLocalRandom.current().nextInt(1, 6);
                    if (randomNumer == 2) {
                        numberOfnaughtyDazzles++;
                        naughtyDazzles.add(dazzle);
                    }
                }
                dazzles.removeAll(naughtyDazzles);
                milkAmount += dazzles.stream().mapToDouble(x -> ((x.getLevel() - 99) * 0.3) * 1.25).sum();
                goats.addAll(boostedBitches);
                goats.addAll(dazzles);
                DecimalFormat df = new DecimalFormat("#.#");
                Milking milking = new Milking();
                milking.setDiscordId(id);
                milkingRepositoryInsert.insertApiEvent(milking);
                StringBuilder sb = new StringBuilder();
                sb.append("<@").append(farmer.getDiscordID()).append(">");
                sb.append("\\n");
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
                int mastitisRandomNumber = 15;
                if (farmersPurchasedPerks.stream().map(Perks::getId).collect(Collectors.toList()).contains(7)) {
                    mastitisRandomNumber = 19;
                }
                else if (farmersPurchasedPerks.stream().map(Perks::getId).collect(Collectors.toList()).contains(3)) {
                    mastitisRandomNumber = 17;
                }
                log.info("Mastitis random number ======> {}", mastitisRandomNumber);
                int randomNum = ThreadLocalRandom.current().nextInt(1, mastitisRandomNumber + 1);
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
                // Maintenance milk boost or reduction

                Optional<Maintenance> farmersMaintenance = maintenanceRepository.findMaintenanceByFarmerid(farmer.getDiscordID());

                double maintenanceMultiplier = 1;
                if (farmersMaintenance.isPresent()) {
                    if (farmersMaintenance.get().isNeedsMaintenance()) {
                        maintenanceMultiplier = 0.75;
                        sb.append("Milk production is reduced as maintenance is needed.");
                        sb.append("\\n");
                    }
                    else if (farmersMaintenance.get().isMilkingBoost()) {
                        maintenanceMultiplier = 1.10;
                        farmersMaintenance.get().setMilkingBoost(false);
                        maintenanceRepository.save(farmersMaintenance.get());
                    }
                }
                log.info("Milk amount before maintenance multiplier of {} =====> {}", maintenanceMultiplier, milkAmount);
                milkAmount = milkAmount * maintenanceMultiplier;
                log.info("Milk amount before perk multiplier of {} ========> {}", perkMultiplier, milkAmount);
                milkAmount = milkAmount * perkMultiplier;
                farmer.setMilk(farmer.getMilk() + milkAmount);
                farmerRepository.save(farmer);
                if (counter > 0){
                    sb.append(counter == 1 ? counter + " goat has levelled up" : counter + " goats have levelled up.");
                }
                if (randomNum == 3) {
                    sb.append("\\n");
                    sb.append("You discover ").append(numberOfGoatsAffected).append(" goats have Mastitis. They've lost up to ").append(+levelsLost).append(" levels as they recover.");
                }
                if (numberOfnaughtyDazzles > 0) {
                    sb.append("\\n");
                    sb.append(numberOfnaughtyDazzles).append(" of your Dazzles were troublesome during milking and ").append("therefore were not milked today.");
                }
                response.setMessage("You have successfully milked " + numberOfGoats +
                        " goats and gained " + df.format(milkAmount) + " lbs of milk"
                        + "\\n"
                        + sb);
            }
        }
        PostMilkExpiry.SendWebhook(response.getMessage());
        return response;
    }
}
