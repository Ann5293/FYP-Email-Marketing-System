package com.ms.email.marketing.repository;

import com.ms.email.marketing.model.CustomerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerModel, Long> {
    List<CustomerModel> findAllByCustomerGroupIdAndStatusNot(Long id, String status);
    CustomerModel findByIdAndCustomerGroupIdAndStatusNot(Long id, Long groupId, String status);

    @Query(nativeQuery = true, value = "select" +
            " count(*) from ems_customer cust " +
            "   right join ems_customer_base custGroup on custGroup.status = ?1 and cust.customer_group_id = custGroup.id " +
            " where cust.status = ?1")
    Long countByStatus(String propertyValue);

    CustomerModel findByEmailAndCustomerGroupIdAndStatus(String email, Long customerGroupId, String status);
}
