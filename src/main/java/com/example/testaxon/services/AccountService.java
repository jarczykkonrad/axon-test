package com.example.testaxon.services;

import com.example.testaxon.commands.CreateAccountCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AccountService {

//    using command gateway

    private final CommandGateway commandGateway;

    @Autowired
    public AccountService(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    public CompletableFuture<String> createAccount(String accountId, String accountHolderName) {
        CreateAccountCommand command = new CreateAccountCommand(accountId, accountHolderName);

        return CompletableFuture.supplyAsync(() -> {
            try {
                commandGateway.sendAndWait(command);
                return "success: " + command.getAccountId();
            } catch (Exception e) {
                return "notsuccess: " + e.getMessage();
            }
        });
    }

}
