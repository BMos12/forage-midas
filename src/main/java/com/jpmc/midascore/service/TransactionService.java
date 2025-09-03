package com.jpmc.midascore.service;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class TransactionService {

    private final DatabaseConduit conduit;
    private final TransactionRecordRepository txRepo;
    private final AtomicBoolean printScheduled = new AtomicBoolean(false);

    public TransactionService(DatabaseConduit conduit,
            TransactionRecordRepository txRepo) {
        this.conduit = conduit;
        this.txRepo = txRepo;
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
        recipient.setBalance(recipient.getBalance() + tx.getAmount());

        // persist user changes and transaction record
        conduit.save(sender);
        conduit.save(recipient);
        txRepo.save(new TransactionRecord(sender, recipient, tx.getAmount()));

        // schedule a delayed print of Waldorfs final balance this prints once
        if (printScheduled.compareAndSet(false, true)) {
            new Thread(() -> {
                try {
                    // wait longer than the tests 2s sleep to ensure all messages arrived
                    Thread.sleep(2500);
                } catch (InterruptedException ignored) {
                }

                // Waldorf was inserted 5th in seed file so his ID = 5
                UserRecord waldorf = conduit.findById(5L);
                int floored = (int) Math.floor(waldorf.getBalance());
                System.out.println(">>> WALDORF FINAL BALANCE (floored): " + floored);
            }).start();
        }
    }
}