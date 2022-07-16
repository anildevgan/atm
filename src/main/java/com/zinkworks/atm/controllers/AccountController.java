package com.zinkworks.atm.controllers;

import com.zinkworks.atm.controllers.mappers.ResponseMapper;
import com.zinkworks.atm.dtos.AccountDto;
import com.zinkworks.atm.services.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{accNo}")
    public AccountDto getBalance(@PathVariable Long accNo, @RequestHeader Integer pin){
        return ResponseMapper.makeBalanceResponse(accountService.getBalance(accNo, pin));
    }

    @PostMapping("/{accNo}")
    public Map<String, Object> dispenseCash(@PathVariable Long accNo,
                                            @RequestHeader Integer pin,
                                            @RequestHeader Long atmId,
                                            @RequestHeader Integer amount){
        return ResponseMapper.makeDispenseResponse(accountService.dispenseCash(accNo, pin, atmId, amount));
    }
}
