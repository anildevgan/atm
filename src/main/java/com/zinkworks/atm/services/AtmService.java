package com.zinkworks.atm.services;

import com.zinkworks.atm.daos.Atm;

public interface AtmService {

    Atm getAtm(Long atmId);
    Atm saveAtm(Atm atm);

}
