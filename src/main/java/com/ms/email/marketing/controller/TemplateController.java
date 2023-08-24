package com.ms.email.marketing.controller;

import com.ms.email.marketing.model.EmailTemplateModel;
import com.ms.email.marketing.repository.EmailTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static com.ms.email.marketing.constant.AppConstant.FLASH_ATTR_ERRORMSG;
import static com.ms.email.marketing.constant.AppConstant.STATUS_DELETED;

@Controller
public class TemplateController {
    private final Logger log = LoggerFactory.getLogger(TemplateController.class);
    @Autowired
    private EmailTemplateRepository emailTemplateRepo;

    @GetMapping("/template")
    public String showEmailTemplateIndexPage(Model model) {
        model.addAttribute("templateListObj", emailTemplateRepo.findAllByStatusNot(STATUS_DELETED));
        return "emailTemplateList";
    }

    @GetMapping("/template/create")
    public String showEmailTemplateCreatePage(Model model) {
        return "emailTemplateCreate";
    }

    @GetMapping("/template/{id}")
    public String showEditEmailTemplatePage(
            Model model
            , @PathVariable("id") String id
            , RedirectAttributes redirectAttributes
    ) {
        Optional<EmailTemplateModel> templateModel = emailTemplateRepo.findById(Long.valueOf(id));
        if (templateModel.isPresent()) {
            log.info("check template id: " + id);
            model.addAttribute("templateObj", templateModel.get());
            return "emailTemplateEdit";
        }
        redirectAttributes.addFlashAttribute(FLASH_ATTR_ERRORMSG, String.format("This email template is not available or not found, Template ID: %s", id));
        return "redirect:/template";
    }

    @PostMapping("/template/{id}")
    public String createEmailTemplate(Model model, @PathVariable("id") String id) {
        Optional<EmailTemplateModel> templateModel = emailTemplateRepo.findById(Long.valueOf(id));
        if (templateModel.isPresent()) {
            log.info("check template id: " + id);
            model.addAttribute("templateObj", templateModel.get());
            return "emailTemplateEdit";
        }
        model.addAttribute("errorMsg", "Template not found or invalid");
        return "emailTemplateList";
    }
    @PostMapping("/template/update")
    public String updateEmailTemplate(
            Model model
            , @ModelAttribute EmailTemplateModel templateModelUpdate
            , RedirectAttributes redirectAttributes) {
        Optional<EmailTemplateModel> templateModel = emailTemplateRepo.findById(templateModelUpdate.getId());
        log.info("PUT template/update: "+ templateModelUpdate.getId());
        log.info("PUT type: "+ templateModelUpdate.getType());
        if (templateModel.isPresent()) {
            log.info("PUT templateId is eixst: " + templateModelUpdate.getId());
            try{
                EmailTemplateModel existTemplate = templateModel.get();
                existTemplate.setName(templateModelUpdate.getName());
                existTemplate.setSubject(templateModelUpdate.getSubject());
                existTemplate.setDescription(templateModelUpdate.getDescription());
                existTemplate.setType(templateModelUpdate.getType());
                existTemplate.setBody(templateModelUpdate.getBody());
                existTemplate = emailTemplateRepo.saveAndFlush(existTemplate);
                redirectAttributes.addFlashAttribute("templateObj", existTemplate);
                redirectAttributes.addFlashAttribute("successMsg", "The template has updated successfully.");
                return "redirect:/template/"+templateModel.get().getId();
            }catch (Exception e){
                log.error("Error update template");
                //redirectAttributes.addAttribute("templateObj", templateModelUpdate);
                redirectAttributes.addAttribute("errorMsg", "Failed to update the template");
                return "redirect:/template/"+templateModelUpdate.getId();
            }
        }
        model.addAttribute("errorMsg", "Template not found or invalid");
        return "emailTemplateList";
    }
}
