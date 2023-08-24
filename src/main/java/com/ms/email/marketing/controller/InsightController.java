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
            @RequestParam(value = "searchTag", required = false) String searchTag,
            @RequestParam(value = "searchCount", required = false) Long searchCount
    ) {
        List<InsightResponse> insightResponseList = new ArrayList<>();
        if (model.getAttribute("insightList") == null) {
            insightResponseList = emailLogRepo.getInsightResponse();
        }
        if (searchCount != null) {
            insightResponseList = emailLogRepo.getInsightResponse(searchCount);
        }
        model.addAttribute("insightList", insightResponseList);

        log.info("searchTag: " + searchTag);
        log.info("searchCount: " + searchCount);

        model.addAttribute("searchTag", searchTag == null ? "" : searchTag);
        model.addAttribute("searchCount", searchCount); // No need to check for null, Long defaults to null if not provided

        return "insight";
    }
}
