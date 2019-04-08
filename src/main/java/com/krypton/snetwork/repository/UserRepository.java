package com.krypton.snetwork.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import com.krypton.snetwork.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    Optional<User> findById(@Param("id") Long id);

    @Query(value = "SELECT email FROM users where users.email = :email",nativeQuery = true)
    Optional<String> findEmail(@Param("email") String email);

    @Query(value = "SELECT * FROM users where users.email = :email",nativeQuery = true)
    User findByEmail(@Param("email") String email);

    @Query(value = "SELECT * FROM users where users.name = :name",nativeQuery = true)
    User findByName(@Param("name") String name);

}
