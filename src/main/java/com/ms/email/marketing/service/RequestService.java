package com.ms.email.marketing.service;

import com.ms.email.marketing.model.BaseRequestModel;
import com.ms.email.marketing.repository.BaseRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestService {
    @Autowired private BaseRequestRepository baseRequestRepository;

    public BaseRequestModel saveAndFlush(BaseRequestModel baseRequestModel){
        return baseRequestRepository.saveAndFlush(baseRequestModel);
    }
}
