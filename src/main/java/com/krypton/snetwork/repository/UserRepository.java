package com.krypton.snetwork.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends Repository<User, Long> {
    
    @Query("SELECT user FROM users where user.id = :id")
    User findById(@Param("id") Long id);

    @Query("SELECT user FROM users where user.email = :email")
    User findByEmail(@Param("email") String email);
}
