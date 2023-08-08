package com.ms.email.marketing.repository;

import com.ms.email.marketing.model.EmailLoggingModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EmailLoggingRepository extends JpaRepository<EmailLoggingModel, Long> {

}
