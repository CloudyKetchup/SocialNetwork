package com.krypton.snetwork.repository;

import com.krypton.snetwork.model.image.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    @Override
    Optional<Image> findById(@Param("id") Long id);

    @Query(value = "select * from images where images.name = :name",nativeQuery = true)
    Image findByName(@Param("name") String name);
}