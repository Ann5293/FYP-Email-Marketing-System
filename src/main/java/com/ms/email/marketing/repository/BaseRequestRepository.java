package com.ms.email.marketing.repository;

import com.ms.email.marketing.model.BaseRequestModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface BaseRequestRepository extends JpaRepository<BaseRequestModel, UUID> {

}
