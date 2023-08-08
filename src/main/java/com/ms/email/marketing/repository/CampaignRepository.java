package com.ms.email.marketing.repository;

import com.ms.email.marketing.model.CampaignModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<CampaignModel, Long> {
    List<CampaignModel> findAllByStatusNot(String status);
}
