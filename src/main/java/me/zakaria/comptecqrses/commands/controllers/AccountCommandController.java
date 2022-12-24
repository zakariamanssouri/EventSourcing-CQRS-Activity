package me.zakaria.comptecqrses.commands.controllers;

import lombok.AllArgsConstructor;
import me.zakaria.comptecqrses.commonapi.commands.CreateAccountCommand;
import me.zakaria.comptecqrses.commonapi.commands.CreditAccountCommand;
import me.zakaria.comptecqrses.commonapi.commands.DebitAccountCommand;
import me.zakaria.comptecqrses.commonapi.dtos.CreateAccountRequestDTO;
import me.zakaria.comptecqrses.commonapi.dtos.CreditAccountRequestDTO;
import me.zakaria.comptecqrses.commonapi.dtos.DebitAccountRequestDTO;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@RestController
@RequestMapping("/commands/account")
@AllArgsConstructor
public class AccountCommandController {
    private CommandGateway commandGateway;
    private EventStore eventStore;

    @PostMapping("/create")
    public CompletableFuture<String> createAccount(@RequestBody CreateAccountRequestDTO requestDTO) throws ExecutionException, InterruptedException {
        CompletableFuture<String> commandResponse = commandGateway.send(new CreateAccountCommand(
                UUID.randomUUID().toString(),
                requestDTO.getInitialBalance(), requestDTO.getCurrency()));
        return commandResponse;
    }

    @PutMapping("/credit")
    public CompletableFuture<String> creditAccount(@RequestBody CreditAccountRequestDTO requestDTO) throws ExecutionException, InterruptedException {
        CompletableFuture<String> commandResponse = commandGateway.send(new CreditAccountCommand(
                requestDTO.getAccountId(),
                requestDTO.getAmount(), requestDTO.getCurrency()));
        return commandResponse;
    }


    @PutMapping("/debit")
    public CompletableFuture<String> creditAccount(@RequestBody DebitAccountRequestDTO requestDTO) {
        CompletableFuture<String> commandResponse = commandGateway.send(new DebitAccountCommand(
                requestDTO.getAccountId(),
                requestDTO.getAmount(), requestDTO.getCurrency()));
        return commandResponse;
    }


    @GetMapping("/eventStore/{accountId}")
    public Stream eventStore(@PathVariable String accountId) {
        return eventStore.readEvents(accountId).asStream();
    }




    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception exception) {
        ResponseEntity<String> responseEntity = new ResponseEntity<>(exception.getMessage(), null, 500);
        return responseEntity;
    }
}
