package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private final TransactionService txService;

    public KafkaConsumer(TransactionService txService) {
        this.txService = txService;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void listen(Transaction tx) {
        // this print below is what i used for the task 2 answer
        // System.out.println("Received transaction amount: " +
        // transaction.getAmount());

        // used in task3
        txService.process(tx);
    }
}