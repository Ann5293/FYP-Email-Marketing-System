package com.ms.email.marketing.model.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
public class CampaignResponse {
    private Long id;
    private String name;
    private String status;
    private Long customerGroupId;
    private Long emailTemplateId;
    private String customerGroupName;
    private String emailTemplateName;
    private String createdDate;
}
