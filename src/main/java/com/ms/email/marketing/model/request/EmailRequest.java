package com.ms.email.marketing.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailRequest {

    private String from;
    private String to;
    private String subject;
    private String message;
    private String type;
    private String trackingUuid; // tracking purpose
    //private Map<String, Object> attachmentMap;
    //private Map<String, String> paramMap;
}
