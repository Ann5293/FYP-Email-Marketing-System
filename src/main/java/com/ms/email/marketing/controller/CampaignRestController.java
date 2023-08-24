package com.ms.email.marketing.controller;


import com.ms.email.marketing.constant.AppConstant;
import com.ms.email.marketing.model.response.CampaignResultResponse;
import com.ms.email.marketing.service.CampaignService;
import com.ms.email.marketing.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = AppConstant.API_VERSION_V1)
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class CampaignRestController {
    private final Logger log = LoggerFactory.getLogger(CampaignRestController.class);

    @Autowired
    private CampaignService campaignService;
    @Autowired private FileService fileService;

    @GetMapping("/campaign")
    public ResponseEntity getAllCampaign() {
        return ResponseEntity.ok(campaignService.getAllCampaignWithCustomResponse());
    }

    @GetMapping("/download/campaign/{id}")
    public ResponseEntity<byte[]> downloadCampaignResultCsv(
            @PathVariable("id") Long campaignId
    ) {
        List<CampaignResultResponse> campaignResultResponses =  campaignService.getCampaignResultResponse(campaignId);
        byte[] csvContent = fileService.generateCampaignResultCsvFile(campaignResultResponses);
        HttpHeaders headers = new HttpHeaders();
        String fileName = campaignId+"_"+campaignResultResponses.get(0).getCampaignName() + "_result.csv";
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);
    }
}
