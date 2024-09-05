package com.example.testaxon.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class CreateAccountCommand {

    @TargetAggregateIdentifier
    private final String accountId; // no need of final in private method, as private methods are inaccessible\ implicitly final
    private final String accountHolderName;

    public CreateAccountCommand(String accountId, String accountHolderName) {
        this.accountId = accountId;
        this.accountHolderName = accountHolderName;
    }
    public java.lang.String getAccountId() {
        return accountId;
    }
    public java.lang.String getAccountHolderName() {
        return accountHolderName;
    }
}
