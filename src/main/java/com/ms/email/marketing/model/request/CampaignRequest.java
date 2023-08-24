package com.ms.email.marketing.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CampaignRequest {
    private String campaignName;
    private Long customerGroupId;
    private Long templateId;
}
