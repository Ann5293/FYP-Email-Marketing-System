package com.ms.email.marketing.model.response;

import java.time.LocalDateTime;

public interface CampaignResultResponse {
    Long getId();

    String getCampaignName();

    String getStatus();

    String getEmail();

    String getEmailStatus();

    String getErrorMsg();

    LocalDateTime getSentDateTime();

}
