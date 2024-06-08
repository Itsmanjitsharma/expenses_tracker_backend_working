package com.securitydemo.jwt_security_demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.securitydemo.jwt_security_demo.entity.Transaction;
import com.securitydemo.jwt_security_demo.repository.TransactionRepository;



@Service
public class TransactionService {


    @Autowired
    TransactionRepository transactionRepository;

    
    private static final Logger logger = LoggerFactory.getLogger(JwtUserDetailsService.class);

    public List<Transaction> findAllByUsername(String username){
        logger.info("find all transaction for this username : "+username);
       return transactionRepository.findAllByUsername(username);
    }

    public void addTransaction(Transaction transaction){
        logger.info("save transaction for username : "+transaction.getUsername());
        transactionRepository.save(transaction);
    }

    public Optional<Transaction> findByIdAndUsername(Long id, String username) {
        throw new UnsupportedOperationException("Unimplemented method 'findByIdAndUsername'");
    }

    public void updateTransaction(Transaction updatedTransaction) {
        throw new UnsupportedOperationException("Unimplemented method 'updateTransaction'");
    }

    public void updateTransaction(Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'updateTransaction'");
    }

    public List<Transaction> findAllByUsernameAndDateRange(String username, LocalDate localDate, LocalDate localDate2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAllByUsernameAndDateRange'");
    }

    public Double getTotalAmountByUsername(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTotalAmountByUsername'");
    }
}
