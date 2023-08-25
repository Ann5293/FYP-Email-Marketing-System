package com.ms.email.marketing.controller;


import com.ms.email.marketing.constant.AppConstant;
import com.ms.email.marketing.model.CampaignModel;
import com.ms.email.marketing.model.CustomerGroupModel;
import com.ms.email.marketing.model.EmailTemplateModel;
import com.ms.email.marketing.model.request.CampaignRequest;
import com.ms.email.marketing.model.response.CampaignResultResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

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
    public String showCampaignPage(
            Model model
            , RedirectAttributes redirectAttributes
    ) {
        return campaignService.handleCampaignPage(model);
    }

    @GetMapping("/campaign/{id}")
    public String showCampaignResultPage(
            @PathVariable("id") Long campaignId
            ,Model model
            , RedirectAttributes redirectAttributes) {
        Optional<CampaignModel> isActiveCampaign = campaignService.getActiveCampaignById(campaignId);
        if(!isActiveCampaign.isPresent() || isActiveCampaign.get().getStatus().equalsIgnoreCase(AppConstant.STATUS_DELETED)){
            redirectAttributes.addFlashAttribute(AppConstant.FLASH_ATTR_ERRORMSG, "Invalid Campaign Id: "+ campaignId);
            return "redirect:/campaign";
        }

        List<CampaignResultResponse> campaignResultResponses =  campaignService.getCampaignResultResponse(campaignId);
        model.addAttribute("campaignObj", isActiveCampaign.get());
        model.addAttribute("campaignResultResponses", campaignResultResponses);
        return "campaignView";
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
        CampaignModel campaignModel = null;
        try{
            campaignModel = emailService.startBlastCampaign(campaignRequest.getTemplateId(), campaignRequest.getCustomerGroupId(), campaignRequest.getCampaignName());
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            errorMsg = e.getMessage();
        }
        if(errorMsg.isEmpty()){
            redirectAttributes.addFlashAttribute("successMsg", "Campaign created.");
            return "redirect:/campaign/"+campaignModel.getId();
        }
        redirectAttributes.addFlashAttribute("errorMsg", errorMsg);
        redirectAttributes.addFlashAttribute("campaignRequest", campaignRequest);
        return "redirect:/campaign/create";
    }
}
