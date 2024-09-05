package com.example.testaxon.queries;

public class AccountSummary {
    private final String accountId;
    private final String accountHolderName;

    public AccountSummary(String accountId, String accountHolderName) {
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
