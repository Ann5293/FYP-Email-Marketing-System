package com.ms.email.marketing.repository;

import com.ms.email.marketing.model.CustomerGroupModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerGroupRepository extends JpaRepository<CustomerGroupModel, Long> {

    List<CustomerGroupModel> findAllByStatusNot(String status);

}
