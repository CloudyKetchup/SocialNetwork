package com.krypton.snetwork.repository;

import com.krypton.snetwork.model.DBFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DBImageRepository extends JpaRepository<DBFile, String> {

}