package com.williamspires.bumble.milking.ScheduledTasks;

import com.williamspires.bumble.milking.Exceptions.CaveNotFoundException;
import com.williamspires.bumble.milking.Exceptions.DairyNotFoundException;
import com.williamspires.bumble.milking.Repositories.AgingRepository;
import com.williamspires.bumble.milking.Repositories.CaveRepository;
import com.williamspires.bumble.milking.Repositories.DairyRepository;
import com.williamspires.bumble.milking.Webhook.PostMilkExpiry;
import com.williamspires.bumble.milking.models.Cave;
import com.williamspires.bumble.milking.models.Dairy;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AgingTask {

    @Autowired
    AgingRepository agingRepository;
    @Autowired
    CaveRepository caveRepository;
    @Autowired
    DairyRepository dairyRepository;

    @Scheduled(cron = "0 9 0 * * *", zone = "GMT")
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
                agingRepository.delete(esc);
                double amountGained = esc.getAmount()/10;
                dairy.setHardcheese(dairy.getHardcheese() + amountGained);
                dairyRepository.save(dairy);
                sb.append("<@" + esc.getDiscordId() + "> gained " + amountGained + " lbs of hard cheese");
                sb.append("\\n");
            } catch (DairyNotFoundException | CaveNotFoundException e) {
                e.printStackTrace();
                agingRepository.delete(esc);
            }
        });
        if (agedCheese.size() > 0 && sb.toString() != null && sb.length() > 3) {
            sb.setLength(sb.length() - 2);
            PostMilkExpiry.SendWebhook(sb.toString());
        }
    }
}
