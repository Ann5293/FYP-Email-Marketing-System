package com.ms.email.marketing.repository;

import com.ms.email.marketing.model.CustomerListingModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerListingRepository extends JpaRepository<CustomerListingModel, Long> {
    List<CustomerListingModel> findAllByFileUploadId(Long id);
    List<CustomerListingModel> findAllByCustomerGroupIdAndStatusNot(Long id, String status);

}
