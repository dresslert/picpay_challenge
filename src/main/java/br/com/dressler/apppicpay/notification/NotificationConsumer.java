package br.com.dressler.apppicpay.notification;

import org.springframework.boot.autoconfigure.pulsar.PulsarProperties.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class NotificationConsumer {
    private RestClient restClient;

    public NotificationConsumer(RestClient.Builder builder) {
        this.restClient = builder
            .baseUrl("https://util.devi.tools/api/v1/notify")
            .build();
    }

    @KafkaListener(topics = "transaction-notification", groupId = "apppicpay")
    public void receiveNotification(Transaction transaction) {

        var response = restClient.get() 
            .retrieve()
            .toEntity(Notification.class);

        if(response.getStatusCode().isError() || !response.getBody().message()) {
            throw new NotificationException("Error sending notification");
        }

    }

}
