package com.ms.email.marketing.repository;

import com.ms.email.marketing.model.CampaignModel;
import com.ms.email.marketing.model.response.CampaignCustomGetLatestCampaignModel;
import com.ms.email.marketing.model.response.CampaignResultResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<CampaignModel, Long> {
    List<CampaignModel> findAllByStatusNot(String status);

//    @Query(nativeQuery = true, value =
//            "SELECT * FROM ems_email_campaign eec WHERE status != 'DELETED' ORDER BY created_date DESC LIMIT 10"
//    )
//    List<CampaignModel> getLatestTop10NonDeletedCampaigns();

    /*
    select
	eec.id , eec.name , eec.status, eet.name, ecb.name
        from ems_email_campaign eec
            left join ems_email_template eet on eec.email_template_id = eet.id
            left join ems_customer_base ecb on eec.customer_group_id = ecb.id
        where eec.status != 'DELETED'
        order by eec.created_date desc
limit 5;
     */
    @Query(nativeQuery = true, value =
            "SELECT " +
                    "eec.id , eec.name , eec.status, eet.name AS 'emailTemplateName', ecb.name AS 'customerGroupName' " +
                    ", eet.type, eec.created_date as 'createdDate'" +
                    "        from ems_email_campaign eec" +
                    "            left join ems_email_template eet on eec.email_template_id = eet.id" +
                    "            left join ems_customer_base ecb on eec.customer_group_id = ecb.id" +
                    "        where eec.status != 'DELETED' and eet.status != 'DELETED' and ecb.status != 'DELETED'" +
                    "        order by eec.created_date desc " +
                    ";"
    )
    List<CampaignCustomGetLatestCampaignModel> getAllActiveCampaigns();
    @Query(nativeQuery = true, value =
            "SELECT " +
                    "eec.id , eec.name , eec.status, eet.name AS 'emailTemplateName', ecb.name AS 'customerGroupName' " +
                    ", eet.type, eec.created_date as 'createdDate'" +
                    "        from ems_email_campaign eec" +
                    "            left join ems_email_template eet on eec.email_template_id = eet.id" +
                    "            left join ems_customer_base ecb on eec.customer_group_id = ecb.id" +
                    "        where eec.status != 'DELETED' and eet.status != 'DELETED' and ecb.status != 'DELETED'" +
                    "        order by eec.created_date desc " +
                    "limit 5;"
    )
    List<CampaignCustomGetLatestCampaignModel> getLatestTop10NonDeletedCampaigns();

    @Query(nativeQuery = true, value =
            "SELECT " +
                    "ecamp.id, ecamp.name as 'campaignName'" +
                    ", ecb.id as 'customerGroupId', ecb.name as 'customerGroupName'" +
                    ", emt.id as 'templateId', emt.name as 'templateName'" +
                    ", ecamp.status , ecust.email, elog.status AS 'emailStatus' , elog.error_msg as 'errorMsg', elog.trigger_date_time as sentDateTime" +
                    " , elog.read_date_time as 'readDateTime'" +
                    "        from ems_email_campaign ecamp" +
                    "            left join ems_email_template emt on emt.id = ecamp.email_template_id" +
                    "            left join ems_customer_base ecb on ecb.id = ecamp.customer_group_id " +
                    "            left join ems_customer ecust on ecust.customer_group_id  = ecb.id" +
                    "            left join ems_email_log elog on elog.campaign_id = ecamp.id and elog.email_to = ecust.email " +
                    "        where ecamp.id = ?1 and ecamp.status != 'DELETED' and emt.status != 'DELETED' and ecb.status != 'DELETED' and ecust.status != 'DELETED'" +
                    ";"
    )
    List<CampaignResultResponse> getCampaignResult(Long id);

}
