package com.williamspires.bumble.milking.ScheduledTasks;

import com.williamspires.bumble.milking.Exceptions.DairyNotFoundException;
import com.williamspires.bumble.milking.Repositories.CurdingRespoitory;
import com.williamspires.bumble.milking.Repositories.DairyRepository;
import com.williamspires.bumble.milking.Webhook.PostMilkExpiry;
import com.williamspires.bumble.milking.models.Curding;
import com.williamspires.bumble.milking.models.Dairy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

@Component
@Slf4j
public class CurdingTask {

    final CurdingRespoitory curdingRespoitory;
    final DairyRepository dairyRepository;

    public CurdingTask(CurdingRespoitory curdingRespoitory, DairyRepository dairyRepository) {
        this.curdingRespoitory = curdingRespoitory;
        this.dairyRepository = dairyRepository;
    }

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
                sb.append("<@").append(cm.getDiscordId()).append("> gained ").append(cm.getAmount() / 10).append(" lbs of soft cheese.");
                sb.append("\\n");
            } catch (DairyNotFoundException e) {
                e.printStackTrace();
                curdingRespoitory.delete(cm);
            }
        });
        if (curdledMilk.size() > 0 && sb.length() > 3) {
            sb.setLength(sb.length() - 2);
            PostMilkExpiry.SendWebhook(sb.toString());
        }
    }
}
