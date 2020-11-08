package com.williamspires.bumble.milking.Webhook;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class PostMilkExpiry {

    private static String url;
    public PostMilkExpiry(@Value("${bumblebot.webhook.url}") String url){
        this.url = url;
    }
    public static void SendWebhook(String data) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();
        String requestJson = "{\n" +
                "  \"username\":\"BumbleBot\",\n" +
                "  \"avatar_url\":\"https://cdn.discordapp.com/attachments/745012634889355264/764883956729905172/bumblebutton.png\",\n" +
                "  \"content\":\"" + data +"\"" +
                "\n}";
        log.info(requestJson);
        log.info("data =========> {}", data);
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        restTemplate.postForEntity(url, entity, String.class);
    }
}
