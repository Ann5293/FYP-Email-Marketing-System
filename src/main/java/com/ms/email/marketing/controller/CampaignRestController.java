package com.ms.email.marketing.controller;


import com.ms.email.marketing.constant.AppConstant;
import com.ms.email.marketing.service.CampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = AppConstant.API_VERSION_V1)
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class CampaignRestController {
    private final Logger log = LoggerFactory.getLogger(CampaignRestController.class);

    @Autowired
    private CampaignService campaignService;

    @GetMapping("/campaign")
    public ResponseEntity getAllCampaign() {
        return ResponseEntity.ok(campaignService.getAllCampaignWithCustomResponse());
    }

}
