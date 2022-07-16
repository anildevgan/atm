package com.zinkworks.atm.controllers.mappers;

import com.zinkworks.atm.daos.Account;
import com.zinkworks.atm.dtos.AccountDto;

import java.util.Map;

import static com.zinkworks.atm.constants.Constants.ACCOUNT;

public class ResponseMapper {

    private ResponseMapper(){

    }

    public static AccountDto makeBalanceResponse(Account account){
        return new AccountDto(account.getAccountNumber(), account.getOpeningBalance(), account.getOverDraft());
    }

    public static Map<String, Object> makeDispenseResponse(Map<String, Object> map){
        AccountDto accountDto= makeBalanceResponse((Account) map.get(ACCOUNT));
        map.put(ACCOUNT,accountDto);
        return map;
    }
}
