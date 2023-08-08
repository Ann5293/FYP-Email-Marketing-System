package com.ms.email.marketing.service;

import com.ms.email.marketing.model.ApiLoggingModel;
import com.ms.email.marketing.repository.ApiLoggingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoggingService {
    private static final Logger log = LoggerFactory.getLogger(LoggingService.class);
    @Autowired
    private ApiLoggingRepository apiLoggingRepository;

    public ApiLoggingModel logApiRequest(String httpMethod, String requestUri, String requestBody) {
        ApiLoggingModel apiLogging = ApiLoggingModel.builder()
                .http_method(httpMethod)
                .request_uri(requestUri)
                .response_body(requestBody)
                .build();
        try {
            apiLogging = apiLoggingRepository.saveAndFlush(apiLogging);
        } catch (Exception e) {
            log.error("Error: {}", e);
        }
        return apiLogging;
    }
}
