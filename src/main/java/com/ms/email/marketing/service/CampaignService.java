package com.ms.email.marketing.service;

import com.ms.email.marketing.constant.AppConstant;
import com.ms.email.marketing.model.CampaignModel;
import com.ms.email.marketing.model.CustomerGroupModel;
import com.ms.email.marketing.model.EmailTemplateModel;
import com.ms.email.marketing.model.response.CampaignCustomGetLatestCampaignModel;
import com.ms.email.marketing.model.response.CampaignResponse;
import com.ms.email.marketing.model.response.CampaignResultResponse;
import com.ms.email.marketing.repository.CampaignRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CampaignService {
    private final Logger log = LoggerFactory.getLogger(CampaignService.class);

    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private TemplateService templateService;
    @Autowired
    private CustomerService customerService;

    public List<CampaignResponse> getAllCampaignWithCustomResponse() {
        List<CampaignModel> campaignModelList = campaignRepository.findAllByStatusNot(AppConstant.STATUS_DELETED);
        List<CampaignResponse> campaignResponseList = mapDTO(campaignModelList);
        return campaignResponseList;
    }

    private List<CampaignCustomGetLatestCampaignModel> getAllActiveCampaignWithCustomResponse() {
        List<CampaignCustomGetLatestCampaignModel> campaignModelList = campaignRepository.getAllActiveCampaigns();
        return campaignModelList;
    }

    private List<CampaignResponse> mapDTO(List<CampaignModel> campaignModelList) {
        List<CampaignResponse> campaignResponseList = new ArrayList<>();
        if (campaignModelList != null) {
            for (CampaignModel camp : campaignModelList) {
                CampaignResponse campaignResponse = new CampaignResponse();
                campaignResponse.setId(camp.getId());
                campaignResponse.setName(camp.getName());
                campaignResponse.setStatus(camp.getStatus());
                campaignResponse.setCustomerGroupId(camp.getCustomerGroupId());
                campaignResponse.setEmailTemplateId(camp.getEmailTemplateId());
                //Set the template name
                EmailTemplateModel templateModel = templateService.getTemplateById(camp.getEmailTemplateId());
                String templateName = templateModel == null ? "" : templateModel.getName();
                campaignResponse.setEmailTemplateName(templateName);
                //Set the customer group name
                CustomerGroupModel custGroupModel = customerService.getCustomerGroupById(camp.getCustomerGroupId());
                String custGroupName = custGroupModel == null ? "" : custGroupModel.getName();
                campaignResponse.setCustomerGroupName(custGroupName);
                campaignResponseList.add(campaignResponse);
            }
        }

        return campaignResponseList;
    }

    public String handleCampaignPage(
            Model model
    ){
        model.addAttribute("campaignList", getAllActiveCampaignWithCustomResponse());
        return "campaignIndex";
    }

    public List<CampaignResultResponse> getCampaignResultResponse(Long id){
        return campaignRepository.getCampaignResult(id);
    }

    public Optional<CampaignModel> getActiveCampaignById(Long campaignId){
        return campaignRepository.findById(campaignId);
    }
}
