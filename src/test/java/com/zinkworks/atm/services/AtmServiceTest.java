package com.zinkworks.atm.services;

import com.zinkworks.atm.daos.Atm;
import com.zinkworks.atm.repositories.AtmRepository;
import com.zinkworks.atm.services.impl.AtmServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static com.zinkworks.atm.constants.Constants.INVALID_ATM_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtmServiceTest {

    @Mock
    private AtmRepository atmRepository;

    AtmService atmService;

    Atm atm;

    @BeforeEach
    void setUp() {
        atmService=new AtmServiceImpl(atmRepository);
        atm=new Atm(1L, 1500L, 10, 30, 30, 20);
    }

    @Test
    void getAtmInvalidID() {
        when(atmRepository.findById(anyLong())).thenReturn(Optional.empty());
        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> atmService.getAtm(1L)
        );
        assertEquals(INVALID_ATM_ID, thrown.getMessage());
    }
    @Test
    void getAtm() {
        when(atmRepository.findById(anyLong())).thenReturn(Optional.of(atm));
        Atm atmResponse=atmService.getAtm(1L);
        assertEquals(1500, atmResponse.getCashTotal());
    }

    @Test
    void saveAtm() {
        when(atmRepository.save(ArgumentMatchers.any(Atm.class))).thenReturn(atm);
        Atm atmResponse = atmService.saveAtm(atm);
        assertNotNull(atmResponse);
        assertEquals(1500, atmResponse.getCashTotal());
    }
}