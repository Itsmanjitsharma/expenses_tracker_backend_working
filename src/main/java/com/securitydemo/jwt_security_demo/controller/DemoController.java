package com.securitydemo.jwt_security_demo.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.securitydemo.jwt_security_demo.config.JwtUtil;
import com.securitydemo.jwt_security_demo.entity.Transaction;
import com.securitydemo.jwt_security_demo.service.JwtUserDetailsService;
import com.securitydemo.jwt_security_demo.service.TransactionService;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@RestController
@Slf4j
public class DemoController {

    @Autowired
    JwtUserDetailsService jwtUserDetailsService; 

    @Autowired
    TransactionService transactionService;

    @Autowired
    private final Tracer tracer;

    public DemoController(Tracer tracer) {
        this.tracer = tracer;
    }
    
    @Autowired
    JwtUtil jwtUtil;

    @Operation(summary = "Get all expenses",description = "Retrive all expenses for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved expenses",
                     content = @Content(schema = @Schema(implementation = Transaction.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/expenses")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getExpenses(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7)); // Remove 'Bearer ' prefix
        Span currentSpan = tracer.currentSpan();
        log.info("TraceId: {}, SpanId: {} :- for the username: {} ",currentSpan.context().traceId(),currentSpan.context().spanId(),username);
       
        List<Transaction> transactions = transactionService.findAllByUsername(username);
        return ResponseEntity.ok().body(transactions);
    }
    @Operation(summary = "Save an expense", description = "Save a new expense for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully saved expense",
                     content = @Content(schema = @Schema(implementation = Transaction.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/expenses")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> saveExpenses(@RequestHeader("Authorization") String token, @RequestBody Transaction transaction) {
        Span currentSpan = tracer.currentSpan();
        log.info("TraceId: {}, SpanId: {} :- ",currentSpan.context().traceId(),currentSpan.context().spanId());
       
        if (!token.isBlank() && !token.isEmpty()) {
            String username = jwtUtil.extractUsername(token.substring(7)); // Remove 'Bearer ' prefix
            log.info("TraceId: {}, SpanId: {} :-  username: {}",currentSpan.context().traceId(),currentSpan.context().spanId(),username);
            transaction.setUsername(username);
            transactionService.addTransaction(transaction);
            return ResponseEntity.ok().body("Transaction done");
        }
        log.info("TraceId: {}, SpanId: {} :-  Tranaction failed for username",currentSpan.context().traceId(),currentSpan.context().spanId());
        return ResponseEntity.ok().body("Transaction failed");
    }

    @Operation(summary = "Get a transaction by ID", description = "Retrieve a specific transaction by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved transaction",
                     content = @Content(schema = @Schema(implementation = Transaction.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @GetMapping("/expenses/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getTransactionById(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        Span currentSpan = tracer.currentSpan();
        String username = jwtUtil.extractUsername(token.substring(7)); // Remove 'Bearer ' prefix
        log.info("TraceId: {}, SpanId: {} :- username : {} ",currentSpan.context().traceId(),currentSpan.context().spanId(),username);
        Optional<Transaction> transaction = transactionService.findByIdAndUsername(id, username);
        if(transaction.isPresent()){
               return ResponseEntity.ok().body(transaction.get());
        }else{
            log.info("TraceId: {}, SpanId: {} :- Transaction not found for username: {}",currentSpan.context().traceId(),currentSpan.context().spanId(),username);
            return ResponseEntity.status(400).body("Transaction not found");
        }
    }

    @Operation(summary = "Update a transaction", description = "Update an existing transaction by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated transaction",
                     content = @Content(schema = @Schema(implementation = Transaction.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @PutMapping("/expenses/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> updateTransaction(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody Transaction transaction) {
        String username = jwtUtil.extractUsername(token.substring(7)); // Remove 'Bearer ' prefix
        Optional<Transaction> existingTransaction = transactionService.findByIdAndUsername(id, username);
        if (existingTransaction.isPresent()) {
            Transaction updatedTransaction = existingTransaction.get();
            updatedTransaction.setAmount(transaction.getAmount());
            updatedTransaction.setCategory(transaction.getCategory());
            updatedTransaction.setDates(transaction.getDates());
            updatedTransaction.setNotes(transaction.getNotes());
            transactionService.updateTransaction(updatedTransaction);
            return ResponseEntity.ok().body("Transaction updated");
        }
        return ResponseEntity.status(404).body("Transaction not found");
    }
    @Operation(summary = "Delete a transaction", description = "Delete a specific transaction by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted transaction"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @DeleteMapping("/expenses/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteTransaction(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        String username = jwtUtil.extractUsername(token.substring(7)); // Remove 'Bearer ' prefix
        Optional<Transaction> transaction = transactionService.findByIdAndUsername(id, username);
        if (transaction.isPresent()) {
            transactionService.updateTransaction(id);
            return ResponseEntity.ok().body("Transaction deleted");
        }
        return ResponseEntity.status(404).body("Transaction not found");
    }
    @Operation(summary = "Get transactions by date range", description = "Retrieve transactions within a specified date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions",
                     content = @Content(schema = @Schema(implementation = Transaction.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/expenses/range")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getTransactionsByDateRange(@RequestHeader("Authorization") String token,
                                                        @RequestParam String startDate,
                                                        @RequestParam String endDate) {
        String username = jwtUtil.extractUsername(token.substring(7)); // Remove 'Bearer ' prefix
        List<Transaction> transactions = transactionService.findAllByUsernameAndDateRange(username, LocalDate.parse(startDate), LocalDate.parse(endDate));
        return ResponseEntity.ok().body(transactions);
    }
    @Operation(summary = "Get total of transactions", description = "Retrieve total of transactions for particular user for all transaction")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions's total",
                     content = @Content(schema = @Schema(implementation = Transaction.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/expenses/total")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getTotalExpenses(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7)); // Remove 'Bearer ' prefix
        Double totalAmount = transactionService.getTotalAmountByUsername(username);
        return ResponseEntity.ok().body(totalAmount);
    }
}
