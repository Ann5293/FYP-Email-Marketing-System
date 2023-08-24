package com.ms.email.marketing.controller;

import com.ms.email.marketing.constant.AppConstant;
import com.ms.email.marketing.model.response.CampaignCustomGetLatestCampaignModel;
import com.ms.email.marketing.repository.CampaignRepository;
import com.ms.email.marketing.repository.CustomerGroupRepository;
import com.ms.email.marketing.repository.CustomerRepository;
import com.ms.email.marketing.repository.EmailTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {
    private final Logger log = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private CustomerGroupRepository customerGroupRepo;
    @Autowired
    private EmailTemplateRepository emailTemplateRepo;
    @Autowired
    private CampaignRepository campaignRepo;
    @Autowired
    private CustomerRepository customerRepo;

    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {
        Long getCountByTemplate = emailTemplateRepo.countByStatus(AppConstant.STATUS_ACTIVE);
        Long getCountByCustomerGroup = customerGroupRepo.countByStatus(AppConstant.STATUS_ACTIVE);
        Long getCountByCustomerGroupCustomer = customerRepo.countByStatus(AppConstant.STATUS_ACTIVE);
        List<CampaignCustomGetLatestCampaignModel> getLatestTop10NonDeletedCampaigns = campaignRepo.getLatestTop10NonDeletedCampaigns();
        model.addAttribute("getCountByTemplate", getCountByTemplate);
        model.addAttribute("getCountByCustomerGroup", getCountByCustomerGroup);
        model.addAttribute("getCountByCustomerGroupCustomer", getCountByCustomerGroupCustomer);
        model.addAttribute("getLatestTop10NonDeletedCampaigns", getLatestTop10NonDeletedCampaigns);
        return "dashboard";
    }
}
