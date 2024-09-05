package com.example.testaxon.controllers;

import com.example.testaxon.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/create")
    public CompletableFuture<String> createAccount(@RequestParam String accountId, @RequestParam String accountHolderName) {
        return accountService.createAccount(accountId, accountHolderName);
    }

}
