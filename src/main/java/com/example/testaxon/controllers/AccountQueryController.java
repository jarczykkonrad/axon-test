package com.example.testaxon.controllers;

import com.example.testaxon.queries.GetAllAccountsQuery;
import com.example.testaxon.queries.AccountSummary;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/accounts")
public class AccountQueryController {

    private final QueryGateway queryGateway;

    @Autowired
    public AccountQueryController (QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    };

    @GetMapping
    public CompletableFuture<List<AccountSummary>> getAllAccounts() {
        return queryGateway.query(new GetAllAccountsQuery(), ResponseTypes.multipleInstancesOf(AccountSummary.class));
    }
}
