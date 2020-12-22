package com.williamspires.bumble.milking.ScheduledTasks;

import com.williamspires.bumble.milking.Exceptions.DairyNotFoundException;
import com.williamspires.bumble.milking.Repositories.CurdingRespoitory;
import com.williamspires.bumble.milking.Repositories.DairyRepository;
import com.williamspires.bumble.milking.Repositories.SoftCheeseExpiryInsertRepository;
import com.williamspires.bumble.milking.Webhook.PostMilkExpiry;
import com.williamspires.bumble.milking.models.Curding;
import com.williamspires.bumble.milking.models.Dairy;
import com.williamspires.bumble.milking.models.SoftCheeseExpiry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Calendar;
import java.util.List;

public class CurdingTask {

    @Autowired
    CurdingRespoitory curdingRespoitory;
    @Autowired
    DairyRepository dairyRepository;
    @Autowired
    SoftCheeseExpiryInsertRepository softCheeseExpiryInsertRepository;

    @Scheduled(cron = "0 15 0 * * *", zone = "GMT")
    private void SortCurdledMilk() {
        List<Curding> curdledMilk = curdingRespoitory.GetCurdledMilk();
        StringBuilder sb = new StringBuilder();
        curdledMilk.forEach(cm -> {
            try {
                Dairy dairy = dairyRepository.findByOwnerId(cm.getDiscordId())
                        .orElseThrow(() -> new DairyNotFoundException(cm.getDiscordId()));
                dairy.setMilk(dairy.getMilk() - cm.getAmount());
                dairy.setSoftcheese(dairy.getSoftcheese() + (cm.getAmount() / 10));
                dairyRepository.save(dairy);
                curdingRespoitory.delete(cm);
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, 21);
                String d = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE);
                SoftCheeseExpiry softCheeseExpiry = new SoftCheeseExpiry();
                softCheeseExpiry.setAmount((int) (cm.getAmount() / 10));
                softCheeseExpiry.setDiscordID(cm.getDiscordId());
                softCheeseExpiry.setExpiryDate(d);
                softCheeseExpiryInsertRepository.insertExpiryEvent(softCheeseExpiry);
                sb.append("<@" + cm.getDiscordId() + "> gained " + (cm.getAmount()/10) + " lbs of soft cheese which will rot on " + d);
                sb.append("\\n");
            } catch (DairyNotFoundException e) {
                e.printStackTrace();
                curdingRespoitory.delete(cm);
            }
        });
        if (curdledMilk.size() > 0 && sb.toString() != null && sb.length() > 3) {
            sb.setLength(sb.length() - 2);
            PostMilkExpiry.SendWebhook(sb.toString());
        }
    }
}
