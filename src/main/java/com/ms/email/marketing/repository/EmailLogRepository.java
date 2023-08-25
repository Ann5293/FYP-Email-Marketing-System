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
            "SELECT eel.email_to AS 'email', count(eel.email_to) AS 'count', " +
                    " GROUP_CONCAT(DISTINCT eec.name ORDER BY eec.name ASC SEPARATOR ', ') AS emailCampaigns " +
                    "   FROM ems_email_campaign eec " +
                    "   LEFT JOIN ems_customer_base ecb on ecb.id = eec.customer_group_id " +
                    "   left join ems_customer ecust on ecust.customer_group_id  = ecb.id " +
                    "   LEFT JOIN ems_email_template eet on eet.id = eec.email_template_id" +
                    "   LEFT JOIN ems_email_log eel ON eel.campaign_id = eec.id and eel.email_to = ecust.email " +
                    "   WHERE " +
                    "       eel.status = 'SUCCESS' " +
                    "       AND ecb.status = 'ACTIVE' " +
                    "       AND eet.status = 'ACTIVE' " +
                    "       AND ecust.status = 'ACTIVE'" +
                    "       AND eel.read_date_time is not null" +
                    "       AND eec.name like %?2% " +
                    "GROUP BY eel.email_to HAVING count(eel.email_to) >= ?1"
    )
    List<InsightResponse> getInsightResponse(Long count, String campaignSearch);

    Optional<EmailLogModel> findByEmailTrackId(String emailTrackId);
}
