package com.williamspires.bumble.milking.ScheduledTasks;

import com.williamspires.bumble.milking.Exceptions.FarmerNotFoundException;
import com.williamspires.bumble.milking.Repositories.FarmerRepository;
import com.williamspires.bumble.milking.Repositories.MilkExpiryRepository;
import com.williamspires.bumble.milking.Webhook.PostMilkExpiry;
import com.williamspires.bumble.milking.models.Farmer;
import com.williamspires.bumble.milking.models.MilkExpiry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("CommentedOutCode")
@Component
@Slf4j
public class MilkExpiryTask {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    MilkExpiryRepository milkExpiryRepository;
    @Autowired
    FarmerRepository farmerRepository;

//    @Scheduled(cron = "1 0 0 * * *", zone = "GMT")
    @SuppressWarnings("unused")
    public void reportCurrentTime() {
        log.info("The time now is {}", dateFormat.format(new Date()));
//        Calendar c = Calendar.getInstance();
//        String d = c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DATE);
//        SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MMM-dd");
        List<MilkExpiry> expiredMilk = milkExpiryRepository.findExpiredMilk();
        Map<String, Double> milkLosses = new HashMap<>();
        expiredMilk.forEach( expiry -> {
            try {
                Farmer farmer = farmerRepository.findById(expiry.getDiscordID())
                        .orElseThrow(() -> new FarmerNotFoundException(expiry.getDiscordID()));
                farmer.setMilk(farmer.getMilk() - expiry.getMilk());
                if (milkLosses.containsKey(farmer.getDiscordID())) {
                    double milk = milkLosses.get(farmer.getDiscordID());
                    milkLosses.remove(farmer.getDiscordID());
                    milkLosses.put(farmer.getDiscordID(), expiry.getMilk() + milk);
                }
                else {
                    milkLosses.put(farmer.getDiscordID(), expiry.getMilk());
                }
                farmerRepository.save(farmer);
                milkExpiryRepository.delete(expiry);
            } catch (FarmerNotFoundException e) {
                //move on
            }

        });
        StringBuilder sb = new StringBuilder();
        milkLosses.forEach((user, loss) -> {
            sb.append("<@").append(user).append("> lost ").append(loss).append(" lbs of milk");
            sb.append("\\n");
        });
        if (expiredMilk.size() > 0 && milkLosses.size() > 0) {
            sb.setLength(sb.length() - 2);
            PostMilkExpiry.SendWebhook(sb.toString());
        }
    }
}
