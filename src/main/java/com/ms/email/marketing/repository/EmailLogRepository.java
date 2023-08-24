package com.ms.email.marketing.repository;

import com.ms.email.marketing.model.EmailLogModel;
import com.ms.email.marketing.model.response.InsightResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface EmailLogRepository extends JpaRepository<EmailLogModel, Long> {

    @Query(nativeQuery = true, value =
            "SELECT eel.email_to AS 'email', count(eel.email_to) AS 'count' " +
                    "FROM ems_email_campaign eec " +
                    "LEFT JOIN ems_email_log eel ON eel.campaign_id = eec.id " +
                    "GROUP BY eel.email_to HAVING count(eel.email_to) > 0"
    )
    List<InsightResponse> getInsightResponse();
    @Query(nativeQuery = true, value =
            "SELECT eel.email_to AS 'email', count(eel.email_to) AS 'count' " +
                    "FROM ems_email_campaign eec " +
                    "LEFT JOIN ems_email_log eel ON eel.campaign_id = eec.id " +
                    "GROUP BY eel.email_to HAVING count(eel.email_to) >= ?1"
    )
    List<InsightResponse> getInsightResponse(Long count);

    Optional<EmailLogModel> findByEmailTrackId(String emailTrackId);
}
