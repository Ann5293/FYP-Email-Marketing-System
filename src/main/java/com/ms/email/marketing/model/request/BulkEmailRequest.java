package com.ms.email.marketing.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BulkEmailRequest{
    private String from;
    private String to;
    private String subject;
    private String message;
    private String type;
    private Long customerListingId;

}
