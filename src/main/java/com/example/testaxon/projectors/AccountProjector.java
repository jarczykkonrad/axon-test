package com.example.testaxon.projectors;

import com.example.testaxon.events.AccountCreatedEvent;
import com.example.testaxon.queries.AccountSummary;
import com.example.testaxon.queries.GetAllAccountsQuery;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

@Component
public class AccountProjector {

    private final List<AccountSummary> accountSummaries = new CopyOnWriteArrayList<>();

    @EventHandler
    public void on(AccountCreatedEvent event) {
        accountSummaries.add(new AccountSummary(event.getAccountId(), event.getAccountHolderName()));
    }

    @QueryHandler
    public List<AccountSummary> handle(GetAllAccountsQuery query) {
        return new ArrayList<>(accountSummaries);
    }
}
