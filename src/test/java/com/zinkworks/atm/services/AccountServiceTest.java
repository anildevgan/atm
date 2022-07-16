package com.zinkworks.atm.services;

import com.zinkworks.atm.daos.Account;
import com.zinkworks.atm.daos.Atm;
import com.zinkworks.atm.exceptions.BadRequestException;
import com.zinkworks.atm.repositories.AccountRepository;
import com.zinkworks.atm.services.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.Optional;

import static com.zinkworks.atm.constants.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AtmService atmService;

    AccountService accountService;

    Account account;

    Atm atm;

    @BeforeEach
    void setUp() {
        accountService = new AccountServiceImpl(accountRepository,atmService);
        account=new Account(1L,123456789L,1234,800,200);
        atm=new Atm(1L, 1500L, 10, 30, 30, 20);
    }

    @Test
    void getBalanceIncorrectCombination() {
        when(accountRepository.findByAccountNumberAndPin(anyLong(),anyInt())).thenReturn(Optional.empty());
        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> accountService.getBalance(123456789L,1234)
        );
        assertEquals(INCORRECT_COMBINATION, thrown.getMessage());
    }

    @Test
    void getBalance() {
        when(accountRepository.findByAccountNumberAndPin(anyLong(),anyInt())).thenReturn(Optional.of(account));
        Account acc=accountService.getBalance(123456789L,1234);
        assertEquals(800, acc.getOpeningBalance());
        assertEquals(200, acc.getOverDraft());
    }

    @Test
    void dispenseCashIncorrectCombination() {
        when(accountRepository.findByAccountNumberAndPin(anyLong(),anyInt())).thenReturn(Optional.empty());
        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> accountService.dispenseCash(123456789L,1234,1L, 1600)
        );
        assertEquals(INCORRECT_COMBINATION, thrown.getMessage());
    }

    @Test
    void dispenseCashAtmDoNotHaveEnoughCash() {
        when(accountRepository.findByAccountNumberAndPin(anyLong(),anyInt())).thenReturn(Optional.of(account));
        when(atmService.getAtm(anyLong())).thenReturn(atm);
        BadRequestException thrown = assertThrows(
                BadRequestException.class,
                () -> accountService.dispenseCash(123456789L,1234,1L, 1600)
        );
        assertTrue(thrown.getMessage().contains("not available in Atm"));
    }

    @Test
    void dispenseCashYourAccountDoNotHaveEnoughCashBalanceAndOverDraftCombined() {
        when(accountRepository.findByAccountNumberAndPin(anyLong(),anyInt())).thenReturn(Optional.of(account));
        when(atmService.getAtm(anyLong())).thenReturn(atm);
        BadRequestException thrown = assertThrows(
                BadRequestException.class,
                () -> accountService.dispenseCash(123456789L,1234,1L, 1500)
        );
        assertTrue(thrown.getMessage().contains("is less than request amount"));
    }

    @Test
    void dispenseCashAtmNotesNotAvailable() {
        when(accountRepository.findByAccountNumberAndPin(anyLong(),anyInt())).thenReturn(Optional.of(account));
        when(atmService.getAtm(anyLong())).thenReturn(atm);
        BadRequestException thrown = assertThrows(
                BadRequestException.class,
                () -> accountService.dispenseCash(123456789L,1234,1L, 798)
        );
        assertEquals(NOTES_NOT_AVAILABLE_FOR_AMOUNT_SPECIFIED, thrown.getMessage());
    }
    // Deduct Cash from Opening Balance (Since Requested mount is 100) and will Dispense minimum no. of Notes ( For 100 will dispense 2*50, not 5*20 or any large number of notes)
    @Test
    void dispenseCashDeductFromOpeningBalance() {
        when(accountRepository.findByAccountNumberAndPin(anyLong(),anyInt())).thenReturn(Optional.of(account));
        when(atmService.getAtm(anyLong())).thenReturn(atm);
        Map<String, Object> map=accountService.dispenseCash(123456789L,1234,1L, 100);
        Account acc= (Account) map.get(ACCOUNT);
        assertEquals(700, acc.getOpeningBalance());
        assertEquals(200, acc.getOverDraft());
        assertTrue(map.containsKey(NO_OF_FIFTY));
        assertEquals(2, map.get(NO_OF_FIFTY));
    }

    // Deduct Cash from Opening Balance and OverDraft (Since Requested mount is 810 more than opening balance) and will Dispense minimum no. of Notes
    @Test
    void dispenseCashDeductFromOpeningBalanceAndOverDraft() {
        when(accountRepository.findByAccountNumberAndPin(anyLong(),anyInt())).thenReturn(Optional.of(account));
        when(atmService.getAtm(anyLong())).thenReturn(atm);
        Map<String, Object> map=accountService.dispenseCash(123456789L,1234,1L, 810);
        Account acc= (Account) map.get(ACCOUNT);
        assertEquals(0, acc.getOpeningBalance());
        assertEquals(190, acc.getOverDraft());
        assertEquals(10, map.get(NO_OF_FIFTY));
        assertEquals(15, map.get(NO_OF_TWENTY));
        assertEquals(1, map.get(NO_OF_TEN));
    }

    @Test
    void dispenseCashDeduct() {
        when(accountRepository.findByAccountNumberAndPin(anyLong(),anyInt())).thenReturn(Optional.of(account));
        when(atmService.getAtm(anyLong())).thenReturn(atm);
        Map<String, Object> map=accountService.dispenseCash(123456789L,1234,1L, 85);
        Account acc= (Account) map.get(ACCOUNT);
        assertEquals(715, acc.getOpeningBalance());
        assertEquals(200, acc.getOverDraft());
        assertEquals(1, map.get(NO_OF_FIFTY));
        assertEquals(1, map.get(NO_OF_TWENTY));
        assertEquals(1, map.get(NO_OF_TEN));
        assertEquals(1, map.get(NO_OF_FIVE));
    }
}