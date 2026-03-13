package com.clbc.repository;

import com.clbc.model.SystemClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemClientRepository extends JpaRepository<SystemClient, String> {
}
