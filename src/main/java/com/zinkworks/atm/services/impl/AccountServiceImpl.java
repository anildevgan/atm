package com.zinkworks.atm.services.impl;

import com.zinkworks.atm.daos.Account;
import com.zinkworks.atm.daos.Atm;
import com.zinkworks.atm.exceptions.BadRequestException;
import com.zinkworks.atm.repositories.AccountRepository;
import com.zinkworks.atm.services.AccountService;
import com.zinkworks.atm.services.AtmService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;

import static com.zinkworks.atm.constants.Constants.*;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AtmService atmService;

    public AccountServiceImpl(AccountRepository accountRepository, AtmService atmService) {
        this.accountRepository = accountRepository;
        this.atmService = atmService;
    }


    @Override
    public Account getBalance(Long accountNumber, Integer pin) {
        return getAccount(accountNumber, pin);
    }

    @Override
    public Map<String, Object> dispenseCash(Long accountNumber, Integer pin, Long atmId, Integer amount) {
        Map<String, Object> map=new TreeMap<>();
        Account account=getAccount(accountNumber, pin);
        Atm atm=atmService.getAtm(atmId);
        if(amount>atm.getCashTotal()){
            throw new BadRequestException("Requested amount "+amount+" is not available in Atm");
        }
        Integer availableInAccount=account.getOpeningBalance()+account.getOverDraft();
        if(amount>availableInAccount){
            throw new BadRequestException("Your account opening balance "+ account.getOpeningBalance()+" plus overdraft balance "+account.getOverDraft()+" is less than request amount " + amount);
        }
        int[] noOfNotes = new int[4];
        noOfNotes[0]=atm.getNoOfFive();
        noOfNotes[1]=atm.getNoOfTen();
        noOfNotes[2]=atm.getNoOfTwenty();
        noOfNotes[3]=atm.getNoOfFifty();
        Integer[] notes=getNotesToDispense(amount,noOfNotes);
        if(notes.length==0){
            throw new BadRequestException(NOTES_NOT_AVAILABLE_FOR_AMOUNT_SPECIFIED);
        }
        if(account.getOpeningBalance()>=amount){
            account.setOpeningBalance(account.getOpeningBalance()-amount);
        }else{
            Integer val=Math.abs(account.getOpeningBalance()-amount);
            account.setOpeningBalance(0);
            account.setOverDraft(account.getOverDraft()-val);
        }
        accountRepository.save(account);
        atm.setNoOfFive(atm.getNoOfFive()-notes[0]);
        atm.setNoOfTen(atm.getNoOfTen()-notes[1]);
        atm.setNoOfTwenty(atm.getNoOfTwenty()-notes[2]);
        atm.setNoOfFifty(atm.getNoOfFifty()-notes[3]);
        atm.setCashTotal(atm.getCashTotal()-amount);
        atmService.saveAtm(atm);
        map.put(ACCOUNT, account);
        if(notes[0]>0){
            map.put(NO_OF_FIVE, notes[0]);
        }
        if(notes[1]>0){
            map.put(NO_OF_TEN, notes[1]);
        }
        if(notes[2]>0){
            map.put(NO_OF_TWENTY, notes[2]);
        }
        if(notes[3]>0){
            map.put(NO_OF_FIFTY, notes[3]);
        }
        return map;
    }
    private Integer[] getNotesToDispense(Integer amount, int[] noOfNotes) {
        int[] notes = {5,10,20,50};
        List<Integer[]> outputNotesPossibilities = allPossibilities(notes, noOfNotes, new int[4], amount, 0);
        int finalSumOfNoOfNotes=0;
        int count=0;
        Integer[] outputNotes = new Integer[0];
        for (Integer[] possibilityOfNotes : outputNotesPossibilities){
            int sumOfNoOfNotes = Arrays.stream(possibilityOfNotes).mapToInt(i->i).sum();
            if (count == 0) {
                finalSumOfNoOfNotes=sumOfNoOfNotes;
                outputNotes=possibilityOfNotes;
            }
            if(sumOfNoOfNotes<=finalSumOfNoOfNotes){
                finalSumOfNoOfNotes=sumOfNoOfNotes;
                outputNotes=possibilityOfNotes;
            }
            count++;
        }
        return outputNotes;
    }

    private List<Integer[]> allPossibilities(int[] notes, int[] noOfNotes, int[] possibilities, int amount, int iteration){
        List<Integer[]> outputNotesPossibilities = new ArrayList<>();
        int value = amountValue(notes, possibilities);
        if (value < amount){
            for (int i = iteration; i < notes.length; i++) {
                if (noOfNotes[i] > possibilities[i]){
                    int[] newPossibilities = possibilities.clone();
                    newPossibilities[i]++;
                    List<Integer[]> newOutputNotesPossibilities = allPossibilities(notes, noOfNotes, newPossibilities, amount, i);
                    if (newOutputNotesPossibilities != null){
                        outputNotesPossibilities.addAll(newOutputNotesPossibilities);
                    }
                }
            }
        } else if (value == amount) {
            outputNotesPossibilities.add(Arrays.stream(possibilities).boxed().toArray(Integer[]::new));
        }
        return outputNotesPossibilities;
    }

    private int amountValue(int[] notes, int[] possibilities){
        int value = 0;
        for (int i = 0; i < possibilities.length; i++) {
            value += notes[i] * possibilities[i];
        }
        return value;
    }

    private Account getAccount(Long accountNumber, Integer pin){
       return accountRepository.findByAccountNumberAndPin(accountNumber,pin).orElseThrow(() -> new EntityNotFoundException(INCORRECT_COMBINATION));
    }
}
