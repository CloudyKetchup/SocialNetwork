package com.krypton.snetwork.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import com.krypton.snetwork.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    Optional<User> findById(@Param("id") Long id);

    @Query(value = "select email from users where users.email = :email",nativeQuery = true)
    Optional<String> findEmail(@Param("email") String email);

    @Query(value = "select * from users where users.email = :email",nativeQuery = true)
    User findByEmail(@Param("email") String email);

    @Query(value = "select * from users where users.name = :name",nativeQuery = true)
    User findByName(@Param("name") String name);

}
