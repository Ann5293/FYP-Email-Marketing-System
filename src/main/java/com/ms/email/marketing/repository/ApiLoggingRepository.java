package com.ms.email.marketing.repository;

import com.ms.email.marketing.model.ApiLoggingModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApiLoggingRepository extends JpaRepository<ApiLoggingModel, UUID> {

}
