package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "transaction_record")
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserRecord sender;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserRecord recipient;

    @Column(nullable = false)
    private Float amount;

    @Column(nullable = false)
    private Instant timestamp = Instant.now();

    @Column(nullable = false)
    private Float incentive;

    protected TransactionRecord() {
    }

    public TransactionRecord(UserRecord sender,
            UserRecord recipient,
            Float amount,
            Float incentive) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.incentive = incentive;
        this.timestamp = Instant.now();
    }

    // getters no setters for immutability
    public Long getId() {
        return id;
    }

    public UserRecord getSender() {
        return sender;
    }

    public UserRecord getRecipient() {
        return recipient;
    }

    public Float getAmount() {
        return amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Float getIncentive() {
        return incentive;
    }
}
// sets up the many to one relationship from each transaction
// back to its two users
