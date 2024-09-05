package com.example.testaxon.events;

public class AccountCreatedEvent {
    private final String accountId;
    private final String accountHolderName;

    public AccountCreatedEvent(String accountId, String accountHolderName) {
        this.accountId = accountId;
        this.accountHolderName = accountHolderName;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }
}
