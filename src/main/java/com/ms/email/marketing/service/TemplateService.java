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

import java.util.*;

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
            emailTemplateModel.setStatus(AppConstant.STATUS_ACTIVE);
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
        log.info("Check TemplateService PUT");
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
            EmailTemplateModel emailTemplateModel = emailTemplateRepository.findByIdAndStatusNot(Long.valueOf(id), AppConstant.STATUS_DELETED);
            if (emailTemplateModel == null)
                throw new Exception("Email template not found!.");

            EmailTemplateModel existTemplate = emailTemplateModel;
            existTemplate.setStatus(AppConstant.STATUS_DELETED);
            emailTemplateRepository.saveAndFlush(existTemplate);

        } catch (Exception e) {
            log.error("Error: {}", e);
            errorMsg = e.getMessage();
        }
        String finalErrorMsg = errorMsg;
        return ResponseEntity.status(finalErrorMsg.isEmpty() ? 200 : 500)
                .body(new LinkedHashMap() {{
                    put("status", (finalErrorMsg.isEmpty() ? "Success" : "Failed"));
                    put("uuid", UUID.randomUUID());
                    put("message", finalErrorMsg);
                }});
    }

    private String emailTemplate(String emailTemplate, Map<String, String> paramMap) {
        return emailTemplate;
    }

}
