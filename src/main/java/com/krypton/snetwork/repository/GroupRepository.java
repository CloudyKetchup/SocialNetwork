package com.krypton.snetwork.repository;

import com.krypton.snetwork.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    @Override
    Optional<Group> findById(@Param("id") Long id);

    @Query(value = "select * from groups where groups.name = :name",nativeQuery = true)
    Group findByName(@Param("name") String name);

}