package com.zinkworks.atm.services;

import com.zinkworks.atm.daos.Account;

import java.util.Map;

public interface AccountService {

    Account getBalance(Long accountNumber, Integer pin);
    Map<String, Object> dispenseCash(Long accountNumber, Integer pin, Long atmId, Integer amount);
}
