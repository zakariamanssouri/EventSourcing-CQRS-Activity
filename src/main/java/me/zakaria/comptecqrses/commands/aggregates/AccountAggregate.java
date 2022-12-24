package me.zakaria.comptecqrses.commands.aggregates;

import me.zakaria.comptecqrses.commonapi.commands.CreateAccountCommand;
import me.zakaria.comptecqrses.commonapi.commands.CreditAccountCommand;
import me.zakaria.comptecqrses.commonapi.commands.DebitAccountCommand;
import me.zakaria.comptecqrses.commonapi.enums.AccountStatus;
import me.zakaria.comptecqrses.commonapi.events.AccountActivatedEvent;
import me.zakaria.comptecqrses.commonapi.events.AccountCreatedEvent;
import me.zakaria.comptecqrses.commonapi.events.AccountCreditedEvent;
import me.zakaria.comptecqrses.commonapi.events.AccountDebitedEvent;
import me.zakaria.comptecqrses.commonapi.exceptions.NegativeAmountException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class AccountAggregate {
    @AggregateIdentifier
    private String accountId;
    private double balance;
    private String currency;
    private AccountStatus status;

    public AccountAggregate() {
        //Required by Axon
    }

    @CommandHandler
    public AccountAggregate(CreateAccountCommand createAccountCommand) {
        if (createAccountCommand.getInitialBalance() < 0) {
            throw new RuntimeException("Initial balance cannot be negative");
        }
        //OK
        AggregateLifecycle.apply(new AccountCreatedEvent(
                createAccountCommand.getId(),
                createAccountCommand.getInitialBalance(),
                createAccountCommand.getCurrency(), AccountStatus.CREATED));
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.accountId = event.getId();
        this.balance = event.getInitialBalance();
        this.currency = event.getCurrency();
        this.status = AccountStatus.CREATED;
        AggregateLifecycle.apply(new AccountActivatedEvent(event.getId(), AccountStatus.ACTIVATED));
    }

    @EventSourcingHandler
    public void on(AccountActivatedEvent event) {
        this.status = event.getStatus();
    }


    @CommandHandler
    public void handle(CreditAccountCommand command) {
        if (command.getAmount() < 0) {
            throw new NegativeAmountException("Amount cannot be negative");
        }
        AggregateLifecycle.apply(new AccountCreditedEvent(command.getId(), command.getAmount(), command.getCurrency()));
    }

    @EventSourcingHandler
    public void on(AccountCreditedEvent event) {
        this.balance += event.getAmount();
    }


    @CommandHandler
    public void handle(DebitAccountCommand command) {
        if (command.getAmount() < 0) {
            throw new NegativeAmountException("Amount cannot be negative");
        }
        if(this.balance<command.getAmount()){
            throw new RuntimeException("Insufficient balance");
        }

        AggregateLifecycle.apply(new AccountDebitedEvent(command.getId(), command.getAmount(), command.getCurrency()));
    }

    @EventSourcingHandler
    public void on(AccountDebitedEvent event) {
        this.balance -= event.getAmount();
    }
}
