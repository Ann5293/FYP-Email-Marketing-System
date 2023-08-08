package com.ms.email.marketing.service;

import com.ms.email.marketing.constant.AppConstant;
import com.ms.email.marketing.model.EmailTemplateModel;
import com.ms.email.marketing.repository.EmailTemplateRepository;
import com.ms.email.marketing.utils.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TemplateService {
    private final Logger log = LoggerFactory.getLogger(TemplateService.class);

    @Autowired
    private EmailTemplateRepository emailTemplateRepository;
    @Autowired
    private CommonUtil commonUtil;

    public List<EmailTemplateModel> getAllEmailTemplate() {
        //return emailTemplateRepository.findAll();
        return emailTemplateRepository.findAllByStatusNot(AppConstant.STATUS_DELETED);
    }

    public EmailTemplateModel getTemplateById(Long id) {
        return emailTemplateRepository.findById(id).orElse(null);
    }

    public ResponseEntity getEmailTemplateById(String id) {
        log.info("ID: {}", id);
        if (id == null || id.isEmpty()) {
            List<EmailTemplateModel> emailTemplateModelList = getAllEmailTemplate();
            return ResponseEntity.ok(emailTemplateModelList);
        }
        EmailTemplateModel emailTemplateModel = null;
        try {
            emailTemplateModel = emailTemplateRepository.findById(Long.valueOf(id)).orElse(null);
            if (emailTemplateModel == null) {
                return ResponseEntity.status(500).body("Email Template not found! ID: " + id);
            }
            return ResponseEntity.ok(emailTemplateModel);
        } catch (Exception e) {
            log.error("Error: " + e);
        }
        return ResponseEntity.status(500).body(String.format("Email Template ID: %s not found!", id));
    }

    public ResponseEntity createEmailTemplate(String requestBody) {
        String errorMsg = "";
        EmailTemplateModel emailTemplateModel = null;
        try {
            errorMsg = "Unable parse the request object";
            emailTemplateModel = (EmailTemplateModel) commonUtil.jsonStringToObjectElseNull(requestBody, EmailTemplateModel.class);
            errorMsg = "Unable save the email template";
            emailTemplateModel = emailTemplateRepository.saveAndFlush(emailTemplateModel);
            errorMsg = "";
        } catch (Exception e) {
            log.error("Error: {}", e);
        }

        if (errorMsg.isEmpty())
            return ResponseEntity.ok(emailTemplateModel);
        return ResponseEntity.status(500).body(errorMsg);
    }

    public ResponseEntity updateEmailTemplate(String id, String requestBody) {
        String errorMsg = "";
        try {
            Optional<EmailTemplateModel> emailTemplateModel = emailTemplateRepository.findById(Long.valueOf(id));
            if (!emailTemplateModel.isPresent())
                throw new Exception("Email template not found!.");
            EmailTemplateModel existTemplate = emailTemplateModel.get();
            EmailTemplateModel emailTemplateToUpdate = (EmailTemplateModel) commonUtil.jsonStringToObjectElseNull(requestBody, EmailTemplateModel.class);
            if (emailTemplateToUpdate == null)
                throw new Exception("Unable to update email template due to error");
            existTemplate.setName(emailTemplateToUpdate.getName());
            existTemplate.setSubject(emailTemplateToUpdate.getSubject());
            existTemplate.setDescription(emailTemplateToUpdate.getDescription());
            existTemplate.setType(emailTemplateToUpdate.getType());
            existTemplate.setBody(emailTemplateToUpdate.getBody());
            emailTemplateRepository.saveAndFlush(existTemplate);
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            log.error("Error: {}", e);
            errorMsg = e.getMessage();
        }
        return ResponseEntity.status(500).body(errorMsg);
    }

    public ResponseEntity deleteEmailTemplate(String id) {
        String errorMsg = "";
        try {
            Optional<EmailTemplateModel> emailTemplateModel = emailTemplateRepository.findById(Long.valueOf(id));
            if (!emailTemplateModel.isPresent())
                throw new Exception("Email template not found!.");

            EmailTemplateModel existTemplate = emailTemplateModel.get();
            existTemplate.setStatus(AppConstant.STATUS_DELETED);
            emailTemplateRepository.saveAndFlush(existTemplate);
            return ResponseEntity.status(200).body("Success deleted");
        } catch (Exception e) {
            log.error("Error: {}", e);
        }
        return ResponseEntity.status(500).body(errorMsg);
    }

    private String emailTemplate(String emailTemplate, Map<String, String> paramMap) {
        return emailTemplate;
    }

}
