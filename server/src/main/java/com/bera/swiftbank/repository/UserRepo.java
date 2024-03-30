package com.bera.swiftbank.repository;

import com.bera.swiftbank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User,Long> {
    Boolean existsByEmail(String email);
    Boolean existsByAccountNo(String accountNo);
    User findByAccountNo(String accountNo);
}
