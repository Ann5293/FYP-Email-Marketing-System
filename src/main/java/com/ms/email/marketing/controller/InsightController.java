package com.ms.email.marketing.controller;

import com.ms.email.marketing.model.response.InsightResponse;
import com.ms.email.marketing.repository.EmailLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class InsightController {
    private final Logger log = LoggerFactory.getLogger(InsightController.class);

    @Autowired
    private EmailLogRepository emailLogRepo;

    @GetMapping("/insight")
    public String insightPage(
            Model model,
            @RequestParam(value = "searchCampaign", required = false) String searchCampaign,
            @RequestParam(value = "searchCount", required = false) Long searchCount
    ) {
        log.info("searchCampaign: " + searchCampaign);
        log.info("searchCount: " + searchCount);

        List<InsightResponse> insightResponseList = new ArrayList<>();
        if (searchCount == null)
            searchCount = Long.valueOf(1);
        if( searchCampaign == null){
            searchCampaign = "";
        }

        insightResponseList = emailLogRepo.getInsightResponse(searchCount, searchCampaign);

        model.addAttribute("insightList", insightResponseList);

        model.addAttribute("searchCampaign", searchCampaign == null ? "" : searchCampaign);
        model.addAttribute("searchCount", searchCount); // No need to check for null, Long defaults to null if not provided

        return "insight";
    }
}
