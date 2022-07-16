package com.zinkworks.atm.services.impl;

import com.zinkworks.atm.daos.Atm;
import com.zinkworks.atm.repositories.AtmRepository;
import com.zinkworks.atm.services.AtmService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

import static com.zinkworks.atm.constants.Constants.INVALID_ATM_ID;

@Service
public class AtmServiceImpl implements AtmService {

    private final AtmRepository atmRepository;

    public AtmServiceImpl(AtmRepository atmRepository) {
        this.atmRepository = atmRepository;
    }

    @Override
    public Atm saveAtm(Atm atm) {
        return atmRepository.save(atm);
    }

    @Override
    public Atm getAtm(Long atmId){
        return atmRepository.findById(atmId).orElseThrow(() -> new EntityNotFoundException(INVALID_ATM_ID));
    }

}
