package com.zinkworks.atm.repositories;

import com.zinkworks.atm.daos.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    Optional<Account> findByAccountNumberAndPin(Long accountNumber, Integer pin);
}
