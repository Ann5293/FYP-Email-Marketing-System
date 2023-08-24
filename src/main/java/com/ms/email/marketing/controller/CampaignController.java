package com.ms.email.marketing.controller;


import com.ms.email.marketing.model.CustomerGroupModel;
import com.ms.email.marketing.model.EmailTemplateModel;
import com.ms.email.marketing.model.request.CampaignRequest;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        return campaignService.handleCampaignPage(model);
    }

    @GetMapping("/campaign/create")
    public String showCampaignCreate(
            Model model
            , @ModelAttribute CampaignRequest campaignRequest
            , RedirectAttributes redirectAttributes
    ) {
        List<CustomerGroupModel> customerGroupList = customerService.getAllCustomerGroup();
        List<EmailTemplateModel> emailTemplateList = templateService.getAllEmailTemplate();
        log.info("customerGroupList: " + customerGroupList.size());
        log.info("emailTemplateList: " + emailTemplateList.size());
        if (model.getAttribute("campaignRequest") == null) {
            model.addAttribute("campaignRequest", new CampaignRequest());
        }
        model.addAttribute("customerGroupList", customerGroupList);
        model.addAttribute("emailTemplateList", emailTemplateList);
        return "campaignCreate";
    }

    @PostMapping("/campaign/create")
    public String createCampaign(
            Model model
            , @ModelAttribute CampaignRequest campaignRequest
            , RedirectAttributes redirectAttributes
    ) {
        log.info("campaignRequest: "+ campaignRequest);
        String errorMsg = "";
        try{
            emailService.startBlastCampaign(campaignRequest.getTemplateId(), campaignRequest.getCustomerGroupId(), campaignRequest.getCampaignName());
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            errorMsg = e.getMessage();
        }
        if(errorMsg.isEmpty()){
            redirectAttributes.addFlashAttribute("successMsg", "Campaign created.");
            return "redirect:/campaign";
        }
        redirectAttributes.addFlashAttribute("errorMsg", errorMsg);
        redirectAttributes.addFlashAttribute("campaignRequest", campaignRequest);
        return "redirect:/campaign/create";
    }
}
