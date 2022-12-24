package me.zakaria.comptecqrses.query.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zakaria.comptecqrses.commonapi.enums.OperationType;
import me.zakaria.comptecqrses.commonapi.events.AccountActivatedEvent;
import me.zakaria.comptecqrses.commonapi.events.AccountCreatedEvent;
import me.zakaria.comptecqrses.commonapi.events.AccountCreditedEvent;
import me.zakaria.comptecqrses.commonapi.events.AccountDebitedEvent;
import me.zakaria.comptecqrses.commonapi.queries.GetAccountQuery;
import me.zakaria.comptecqrses.commonapi.queries.GetAllAccountsQuery;
import me.zakaria.comptecqrses.query.entities.Account;
import me.zakaria.comptecqrses.query.entities.Operation;
import me.zakaria.comptecqrses.query.repositories.AccountRepository;
import me.zakaria.comptecqrses.query.repositories.OperationRepository;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AccountServiceHandler {

    private AccountRepository accountRepository;
    private OperationRepository operationRepository;

    @EventHandler
    public void on(AccountCreatedEvent event) {
        log.info("Handling AccountCreatedEvent for account id: {}", event.getId());
        Account account = new Account();
        account.setId(event.getId());
        account.setBalance(event.getInitialBalance());
        account.setCurrency(event.getCurrency());
        account.setStatus(event.getStatus());

        accountRepository.save(account);

    }

    @EventHandler
    public void on(AccountActivatedEvent event) {
        log.info("Handling AccountActivatedEvent for account id: {}", event.getId());
        Account account = accountRepository.findById(event.getId()).get();
        account.setStatus(event.getStatus());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountDebitedEvent event) {
        log.info("Handling AccountDebitedEvent for account id: {}", event.getId());
        Account account = accountRepository.findById(event.getId()).get();
        Operation operation = new Operation();
        operation.setAmount(event.getAmount());
        operation.setDate(new Date());
        operation.setType(OperationType.DEBIT);
        operation.setAccount(account);
        operationRepository.save(operation);
        account.setBalance(account.getBalance()- event.getAmount());
        accountRepository.save(account);
    }
    @EventHandler
    public void on(AccountCreditedEvent event) {
        log.info("Handling AccountCreditedEvent for account id: {}", event.getId());
        Account account = accountRepository.findById(event.getId()).get();
        Operation operation = new Operation();
        operation.setAmount(event.getAmount());
        operation.setDate(new Date());
        operation.setType(OperationType.CREDIT);
        operation.setAccount(account);
        operationRepository.save(operation);
        account.setBalance(account.getBalance()+event.getAmount());
        accountRepository.save(account);
    }

    @QueryHandler
    public List<Account> on(GetAllAccountsQuery query) {
        return accountRepository.findAll();
    }

    @QueryHandler
    public Account on(GetAccountQuery query) {
        return accountRepository.findById(query.getId()).get();
    }



}
