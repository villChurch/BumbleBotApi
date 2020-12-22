package com.williamspires.bumble.milking.ScheduledTasks;

import com.williamspires.bumble.milking.Exceptions.DairyNotFoundException;
import com.williamspires.bumble.milking.Repositories.DairyRepository;
import com.williamspires.bumble.milking.Repositories.SoftCheeseExpiryRepository;
import com.williamspires.bumble.milking.Webhook.PostMilkExpiry;
import com.williamspires.bumble.milking.models.Dairy;
import com.williamspires.bumble.milking.models.SoftCheeseExpiry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public class SoftCheeseTask {

    @Autowired
    SoftCheeseExpiryRepository softCheeseExpiryRepository;
    @Autowired
    DairyRepository dairyRepository;

    @Scheduled(cron = "0 10 0 * * *", zone = "GMT")
    private void RemoveExpiredCheese() {
        List<SoftCheeseExpiry> expiredSoftCheese = softCheeseExpiryRepository.GetRottenSoftCheese();
        StringBuilder sb = new StringBuilder();
        expiredSoftCheese.forEach(esc -> {
            try {
                Dairy dairy = dairyRepository.findByOwnerId(esc.getDiscordID())
                        .orElseThrow(() -> new DairyNotFoundException(esc.getDiscordID()));
                dairy.setSoftcheese(dairy.getSoftcheese() - esc.getAmount());
                dairyRepository.save(dairy);
                softCheeseExpiryRepository.delete(esc);
                sb.append("<@" + esc.getDiscordID() + "> lost " + esc.getAmount() + " lbs of soft cheese");
                sb.append("\\n");
            } catch (DairyNotFoundException e) {
                e.printStackTrace();
                softCheeseExpiryRepository.delete(esc);
            }
        });
        if (expiredSoftCheese.size() > 0 && sb.toString() != null && sb.length() > 3) {
            sb.setLength(sb.length() - 2);
            PostMilkExpiry.SendWebhook(sb.toString());
        }
    }
}
