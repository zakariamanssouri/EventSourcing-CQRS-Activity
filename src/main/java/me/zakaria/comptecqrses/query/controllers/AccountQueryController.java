package me.zakaria.comptecqrses.query.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zakaria.comptecqrses.commonapi.queries.GetAccountQuery;
import me.zakaria.comptecqrses.commonapi.queries.GetAllAccountsQuery;
import me.zakaria.comptecqrses.query.entities.Account;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/query/accounts")
@AllArgsConstructor
@Slf4j
public class AccountQueryController {
    private QueryGateway queryGateway;



    @GetMapping("/allAccounts")
    public List<Account> accountList(){
        return queryGateway.query(new GetAllAccountsQuery(), ResponseTypes.multipleInstancesOf(Account.class)).join();
    }

    @GetMapping("/{id}")
    public Account getAccount(@PathVariable String id){
        return queryGateway.query(new GetAccountQuery(id), ResponseTypes.instanceOf(Account.class)).join();
    }


}
