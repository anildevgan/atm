package com.zinkworks.atm.repositories;

import com.zinkworks.atm.daos.Atm;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtmRepository extends CrudRepository<Atm, Long> {

}
