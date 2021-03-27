package com.williamspires.bumble.milking.ScheduledTasks;

import com.williamspires.bumble.milking.Repositories.CookingDoesRepository;
import com.williamspires.bumble.milking.Repositories.DeadGoatInsetRepository;
import com.williamspires.bumble.milking.Repositories.GoatRepository;
import com.williamspires.bumble.milking.Repositories.NewBornKidInsert;
import com.williamspires.bumble.milking.Webhook.PostMilkExpiry;
import com.williamspires.bumble.milking.models.CookingDoes;
import com.williamspires.bumble.milking.models.DeadGoats;
import com.williamspires.bumble.milking.models.Goats;
import com.williamspires.bumble.milking.models.NewBornKids;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BreedingTask {

    @Autowired
    CookingDoesRepository cookingDoesRepository;
    @Autowired
    GoatRepository goatRepository;
    @Autowired
    NewBornKidInsert newBornKidInsert;
    @Autowired
    DeadGoatInsetRepository deadGoatInsetRepository;

    @Scheduled(cron = "0 58 0 * * *", zone = "GMT")
    public void BreedingScheduledTask() {
        List<Integer> doesReadyIds = cookingDoesRepository.doesReady()
                .stream()
                .map(CookingDoes::getGoatid)
                .collect(Collectors.toList());
        List<Goats> doesReadyAsGoats = goatRepository.findAll()
                .stream()
                .filter(doe -> doesReadyIds.contains(doe.getId()))
                .collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        if (doesReadyAsGoats.size() > 0) {
            for (Goats doe : doesReadyAsGoats) {
                int goatSurvives = ThreadLocalRandom.current().nextInt(1, 61);
                if (goatSurvives == 10) {
                    DeadGoats deadGoat = new DeadGoats();
                    deadGoat.setName(doe.getName());
                    deadGoat.setBreed(doe.getBreed());
                    deadGoat.setBaseColour(doe.getBasecolour());
                    deadGoat.setLevel(doe.getLevel());
                    deadGoat.setOwnerId(doe.getOwnerId());
                    deadGoat.setImageLink(doe.getImageLink());
                    deadGoat.setGoatid(doe.getId());
                    deadGoatInsetRepository.insertWithEntityManager(deadGoat);
                    sb.append("<@").append(doe.getOwnerId()).append(">")
                            .append("Oh no! Your goat has died from birthing complications. Rest in peace ")
                            .append(doe.getName());
                    sb.append("\\n");
                    goatRepository.delete(doe);
                } else {
                    int numberOfKiddsToMake = ThreadLocalRandom.current().nextInt(1, 5);
                    sb.append("<@").append(doe.getOwnerId()).append("> ").append(doe.getName()).append(" has produced ").append(numberOfKiddsToMake).append(" new kids");
                    sb.append("\\n");
                    for (int i = 0; i < numberOfKiddsToMake; i++) {
                        newBornKidInsert.saveKiddToDB(GenerateNewKid(doe));
                    }
                }
            }
            PostMilkExpiry.SendWebhook(sb.toString());
            cookingDoesRepository.deleteInBatch(cookingDoesRepository.doesReady());
        }
    }

    private NewBornKids GenerateNewKid(Goats mother) {
        NewBornKids newKid = new NewBornKids();
        newKid.setMother(mother.getId());
        newKid.setBreed(mother.getBreed());
        newKid.setName("Baby goat");
        newKid.setOwnerId(mother.getOwnerId());
        int randomNum = ThreadLocalRandom.current().nextInt(70, 100);
        newKid.setLevel(randomNum);
        randomNum = ThreadLocalRandom.current().nextInt(0, 5);
        if (randomNum == 0) {
            newKid.setBasecolour("White");
        }
        else if (randomNum == 1) {
            newKid.setBasecolour("Black");
        }
        else if (randomNum == 2) {
            newKid.setBasecolour("Red");
        }
        else if (randomNum == 3) {
            newKid.setBasecolour("Chocolate");
        }
        else {
            newKid.setBasecolour("Gold");
        }
        newKid.setImageLink(GetKidImage(newKid.getBreed(), newKid.getBasecolour()));
        return newKid;
    }

    private String GetKidImage(String breed, String baseColour) {
        String goat;
        if (breed.equalsIgnoreCase("nubian")) {
            goat = "NBkid";
        }
        else if (breed.equalsIgnoreCase("nigerian_dwarf")) {
            goat = "NDkid";
        }
        else {
            goat = "LMkid";
        }
        return "Goat_Images/Kids/" + goat + baseColour.toLowerCase() + ".png";
    }
}
