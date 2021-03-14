package com.williamspires.bumble.milking.ScheduledTasks;

import com.williamspires.bumble.milking.Exceptions.CaveNotFoundException;
import com.williamspires.bumble.milking.Exceptions.DairyNotFoundException;
import com.williamspires.bumble.milking.Repositories.AgingRepository;
import com.williamspires.bumble.milking.Repositories.CaveRepository;
import com.williamspires.bumble.milking.Repositories.DairyRepository;
import com.williamspires.bumble.milking.Webhook.PostMilkExpiry;
import com.williamspires.bumble.milking.models.Aging;
import com.williamspires.bumble.milking.models.Cave;
import com.williamspires.bumble.milking.models.Dairy;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AgingTask {

    @Autowired
    AgingRepository agingRepository;
    @Autowired
    CaveRepository caveRepository;
    @Autowired
    DairyRepository dairyRepository;

    @Scheduled(cron = "0 19 0 * * *", zone = "GMT")
    private void AgeSomeCheese() {
        var agedCheese = agingRepository.GetAgedCheese();
        StringBuilder sb = new StringBuilder();
        agedCheese.forEach(esc -> {
            try {
                Cave cave = caveRepository.findById(esc.getDiscordId())
                        .orElseThrow(() -> new CaveNotFoundException(esc.getDiscordId()));
                Dairy dairy = dairyRepository.findByOwnerId(esc.getDiscordId())
                        .orElseThrow(() -> new DairyNotFoundException(esc.getDiscordId()));
                cave.setSoftcheese(cave.getSoftcheese() - esc.getAmount());
                caveRepository.save(cave);
                double amountGained = esc.getAmount()/10;
                dairy.setHardcheese(dairy.getHardcheese() + amountGained);
                dairyRepository.save(dairy);
                sb.append("<@" + esc.getDiscordId() + "> gained " + amountGained + " lbs of hard cheese");
                sb.append("\\n");
            } catch (DairyNotFoundException | CaveNotFoundException e) {
                log.info("Cheese aging has failed as the Dairy or cave could not be found {}", e.getMessage());
                e.printStackTrace();
            }
            catch (Exception e) {
                log.error("Cheese aging has failed with the following message {}", e.getMessage());
                e.printStackTrace();
            }
            finally {
                agingRepository.delete(esc);
            }
        });
        if (agedCheese.size() > 0 && sb.toString() != null && sb.length() > 3) {
            sb.setLength(sb.length() - 2);
            PostMilkExpiry.SendWebhook(sb.toString());
        }
    }

    @Scheduled(cron = "0 25 0 * * *", zone = "GMT")
    private void FixNonAgedCheese() {
        var caves = caveRepository.findAll();
        StringBuilder sb = new StringBuilder();
        Map<Cave, Double> playersCheese = new HashMap<>();
        caves.forEach(cave -> {
            var aging = agingRepository.findAgingByDiscordId(cave.getOwnerId()).size() == 0 ? 0 :
                    agingRepository.findAgingByDiscordId(cave.getOwnerId()).stream().mapToDouble(o -> o.getAmount()).sum();
            playersCheese.putIfAbsent(cave, aging);
        });
        log.info("Found {} caves", caves.size());
        playersCheese.forEach((c, a) -> {
            if (c.getSoftcheese() != a && (c.getSoftcheese() - a) > 0) {
                try {
                    var dairy = dairyRepository.findByOwnerId(c.getOwnerId()).orElseThrow(() -> new DairyNotFoundException(c.getOwnerId()));
                    var amountToAdd = c.getSoftcheese() - a;
                    var amountGained = amountToAdd / 10;
                    log.info("Cave with owener id {} has {} missing hard cheese", c.getOwnerId(), amountGained);
                    c.setSoftcheese(c.getSoftcheese() - amountToAdd);
                    caveRepository.save(c);
                    dairy.setHardcheese(dairy.getHardcheese() + amountGained);
                    dairyRepository.save(dairy);
                    sb.append("<@" + dairy.getOwnerId() + "> gained " + amountGained + " lbs of hard cheese");
                    sb.append("\\n");
                } catch (DairyNotFoundException e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
            }
        });
        if (!sb.toString().isEmpty() && sb.toString() != null && sb.length() > 3) {
            sb.setLength(sb.length() - 2);
            PostMilkExpiry.SendWebhook(sb.toString());
        }
    }
}
