package com.securitydemo.jwt_security_demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Table(name = "transaction")
@Entity
public class Transaction {
       @Id
       @GeneratedValue(strategy = GenerationType.AUTO)
       private int transaction_id;
       private double amount;
       private String category;
       private String notes;
       private String dates;
       private String username;
}
