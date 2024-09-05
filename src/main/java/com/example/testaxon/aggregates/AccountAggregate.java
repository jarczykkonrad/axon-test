package com.example.testaxon.aggregates;

import com.example.testaxon.commands.CreateAccountCommand;
import com.example.testaxon.events.AccountCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;


@Aggregate
public class AccountAggregate {

    @AggregateIdentifier
    private String accountId;
    private String accountHolderName;

    @CommandHandler
    public AccountAggregate(CreateAccountCommand command) {
        apply(new AccountCreatedEvent(command.getAccountId(), command.getAccountHolderName()));
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.accountId = event.getAccountId();
        this.accountHolderName = event.getAccountHolderName();
    }

    protected AccountAggregate() {
    }
}
