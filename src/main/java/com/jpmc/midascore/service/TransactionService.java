package com.jpmc.midascore.service;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class TransactionService {

    private final DatabaseConduit conduit;
    private final TransactionRecordRepository txRepo;
    private final RestTemplate rest;
    private final AtomicBoolean printScheduled = new AtomicBoolean(false);

    public TransactionService(DatabaseConduit conduit,
            TransactionRecordRepository txRepo,
            RestTemplate rest) {
        this.conduit = conduit;
        this.txRepo = txRepo;
        this.rest = rest;
    }

    @Transactional
    public void process(Transaction tx) {
        // lookup the sender and recipient
        UserRecord sender = conduit.findById(tx.getSenderId());
        UserRecord recipient = conduit.findById(tx.getRecipientId());
        if (sender == null || recipient == null)
            return;

        // funds check
        if (sender.getBalance() < tx.getAmount())
            return;

        // update balances
        sender.setBalance(sender.getBalance() - tx.getAmount());
        conduit.save(sender);

        // ask incentive API for bonus
        Incentive inc = rest.postForObject(
                "http://localhost:8080/incentive", tx, Incentive.class);
        float bonus = (inc != null ? inc.getAmount() : 0f);

        // need to credit the recipient with tx amount + bonus
        recipient.setBalance(
                recipient.getBalance() + tx.getAmount() + bonus);
        conduit.save(recipient);

        // persist user changes and transaction record
        txRepo.save(new TransactionRecord(
                sender, recipient, tx.getAmount(), bonus));

        // schedule a delayed print of Waldorfs final balance this prints once
        if (printScheduled.compareAndSet(false, true)) {
            new Thread(() -> {
                try {
                    // wait longer than the tests 2s sleep to ensure all messages arrived
                    Thread.sleep(2500);
                } catch (InterruptedException ignored) {
                }

                // Waldorf was inserted 5th in seed file so his ID = 5
                // Wilbur is 9L
                UserRecord wilbur = conduit.findById(9L);
                int floored = (int) Math.floor(wilbur.getBalance());
                System.out.println(">>> WILBUR FINAL BALANCE (floored): " + floored);
            }).start();
        }
    }
}