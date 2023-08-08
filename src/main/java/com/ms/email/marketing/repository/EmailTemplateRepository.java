package com.ms.email.marketing.repository;

import com.ms.email.marketing.model.EmailTemplateModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplateModel, Long> {
    List<EmailTemplateModel> findAllByStatusNot(String status);

    long countByStatus(String propertyValue);

}
