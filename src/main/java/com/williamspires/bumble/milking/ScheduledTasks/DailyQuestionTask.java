package com.williamspires.bumble.milking.ScheduledTasks;

import com.williamspires.bumble.milking.Repositories.DailyQuestionRepository;
import com.williamspires.bumble.milking.Webhook.PostMilkExpiry;
import com.williamspires.bumble.milking.models.DailyQuestion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DailyQuestionTask {


    @Value("${bumblebot.dailyquestion.webhook.url}")
    private String url;
    @Autowired
    DailyQuestionRepository dailyQuestionRepository;

    @Scheduled(cron = "0 0 19 * * *", zone = "UTC")
    public void PostDailyQuestionTask() {
        List<String> questions = dailyQuestionRepository.findAll()
                .stream()
                .map(DailyQuestion::getQuestion)
                .distinct()
                .collect(Collectors.toList());
        Random rand = new Random();
        int value = rand.nextInt(questions.size());
        log.info("Question: {}", questions.get(value));
        PostMilkExpiry.SendWebhook(questions.get(value), url);
    }

}
