package com.ms.email.marketing.model.response;

import java.time.LocalDateTime;
import java.util.Date;

public interface CampaignCustomGetLatestCampaignModel {
    Long getId();
    String getName();
    String getStatus();
    String getEmailTemplateName();
    String getCustomerGroupName();
    String getType();
    LocalDateTime getCreatedDate();
}
