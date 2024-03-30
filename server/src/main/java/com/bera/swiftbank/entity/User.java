package com.bera.swiftbank.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String firtsName;
    private String lastName;
    private String phoneNumber;
    private String gender;
    private String address;
    private String accountNo;
    private BigDecimal balance;
    private String status;
    @CreationTimestamp
    private String createdAt;
    @UpdateTimestamp
    private String modifiedAt;


}
