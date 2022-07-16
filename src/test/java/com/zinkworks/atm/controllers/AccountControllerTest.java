package com.zinkworks.atm.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zinkworks.atm.daos.Account;
import com.zinkworks.atm.dtos.AccountDto;
import com.zinkworks.atm.exceptions.BadRequestException;
import com.zinkworks.atm.exceptions.ExceptionMapper;
import com.zinkworks.atm.services.AccountService;
import org.json.HTTP;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.persistence.EntityNotFoundException;

import java.util.Map;
import java.util.TreeMap;

import static com.zinkworks.atm.constants.Constants.ACCOUNT;
import static com.zinkworks.atm.constants.Constants.NO_OF_FIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AccountController.class})
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    AccountController accountController;

    @MockBean
    AccountService accountService;

    Account account;

    @BeforeEach
    void setUp() {
        accountController=new AccountController(accountService);
        account=new Account(1L,123456789L,1234,800,200);
        this.mockMvc= MockMvcBuilders.standaloneSetup(accountController)
                .setControllerAdvice( new ExceptionMapper()).build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getBalanceNotFound() throws Exception {
        when(accountService.getBalance(anyLong(),anyInt())).thenThrow(EntityNotFoundException.class);
        MockHttpServletRequestBuilder req=MockMvcRequestBuilders.get("/v1/accounts/1").header("pin","1234");
        MvcResult result=mockMvc.perform(req).andExpect(status().isNotFound()).andReturn();
        assertEquals(HttpStatus.NOT_FOUND.value(),result.getResponse().getStatus());
    }

    @Test
    void getBalance() throws Exception {
        when(accountService.getBalance(anyLong(),anyInt())).thenReturn(account);
        MockHttpServletRequestBuilder req=MockMvcRequestBuilders.get("/v1/accounts/1").header("pin","1234");
        MvcResult result=mockMvc.perform(req).andExpect(status().isOk()).andReturn();
        assertEquals(HttpStatus.OK.value(),result.getResponse().getStatus());
        AccountDto dto=convertJsonToObj(result.getResponse().getContentAsString(), AccountDto.class);
        assertEquals(800,dto.getOpeningBalance());
    }

    @Test
    void dispenseCashBadRequest() throws Exception {
        when(accountService.dispenseCash(anyLong(),anyInt(),anyLong(),anyInt())).thenThrow(BadRequestException.class);
        MockHttpServletRequestBuilder req=MockMvcRequestBuilders.post("/v1/accounts/1")
                .header("pin","1234")
                .header("atmId","1")
                .header("amount","1234");
        MvcResult result=mockMvc.perform(req).andExpect(status().isBadRequest()).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(),result.getResponse().getStatus());
    }

    @Test
    void dispenseCash() throws Exception {
        Map<String, Object> map=new TreeMap<>();
        map.put(ACCOUNT,account);
        map.put(NO_OF_FIVE,1);
        when(accountService.dispenseCash(anyLong(),anyInt(),anyLong(),anyInt())).thenReturn(map);
        MockHttpServletRequestBuilder req=MockMvcRequestBuilders.post("/v1/accounts/1")
                .header("pin","1234")
                .header("atmId","1")
                .header("amount","1234");
        MvcResult result=mockMvc.perform(req).andExpect(status().isOk()).andReturn();
        assertEquals(HttpStatus.OK.value(),result.getResponse().getStatus());
        Map<String, Object> mapResponse=convertJsonToObj(result.getResponse().getContentAsString(), Map.class);
        assertEquals(1,mapResponse.get(NO_OF_FIVE));
    }
    private static <T> T convertJsonToObj(String str, Class<T> genericType) {
        T genericRequest=null;
        try {
            genericRequest= new ObjectMapper().readValue(str, genericType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return genericRequest;
    }

}