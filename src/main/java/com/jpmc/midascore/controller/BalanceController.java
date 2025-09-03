package com.jpmc.midascore.controller;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {

    private final DatabaseConduit conduit;

    public BalanceController(DatabaseConduit conduit) {
        this.conduit = conduit;
    }

    /*
     * responds to GET /balance?userId={id}
     * returns a Balance object (JSON) with 0 if the user isn't found
     */
    @GetMapping("/balance")
    public Balance getBalance(@RequestParam("userId") long userId) {
        UserRecord user = conduit.findById(userId);
        float amount = (user != null) ? user.getBalance() : 0f;
        return new Balance(amount);
    }
}