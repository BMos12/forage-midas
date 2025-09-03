package com.jpmc.midascore.foundation;

public class Incentive {
    private float amount;

    public Incentive() {
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}

// command to run service in other terminal
// java -jar services/transaction-incentive-api.jar
// should be on http://localhost:8080/incentive