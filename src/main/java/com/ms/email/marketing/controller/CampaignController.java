package com.ms.email.marketing.controller;


import com.ms.email.marketing.model.CustomerGroupModel;
import com.ms.email.marketing.model.EmailTemplateModel;
import com.ms.email.marketing.service.CampaignService;
import com.ms.email.marketing.service.CustomerService;
import com.ms.email.marketing.service.EmailService;
import com.ms.email.marketing.service.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CampaignController {
    private final Logger log = LoggerFactory.getLogger(CampaignController.class);

    @Autowired
    private EmailService emailService;
    @Autowired
    private CampaignService campaignService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private TemplateService templateService;

    @GetMapping("/campaign")
    public String showCampaignPage(Model model) {
        return "campaignCreate";
    }

    @GetMapping("/campaign/create")
    public String showCampaignCreate(Model model) {
        List<CustomerGroupModel> customerGroupList = customerService.getAllCustomerGroup();
        List<EmailTemplateModel> emailTemplateList = templateService.getAllEmailTemplate();
        log.info("customerGroupList: "+customerGroupList.size());
        log.info("emailTemplateList: "+emailTemplateList.size());

        model.addAttribute("customerGroupList", customerGroupList);
        model.addAttribute("emailTemplateList", emailTemplateList);

        return "campaignCreate";
    }

    @PostMapping("/campaign/create")
    public String createCampaign(
            Model model
            , @RequestParam(value = "selectedTemplateId", required = false) String templateId
            , @RequestParam(value = "selectedGroupId", required = false) String grouptId
    ) {
        log.info("templateId: {}", templateId);
        log.info("grouptId: {}", grouptId);
        emailService.startBlastCampaign(Long.valueOf(templateId), Long.valueOf(grouptId));
        return "redirect:/campaign/create";
    }
}
