package com.ms.email.marketing.controller;

import com.ms.email.marketing.constant.AppConstant;
import com.ms.email.marketing.service.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = AppConstant.API_VERSION_V1)
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class TemplateRestController {
    private final Logger LOG = LoggerFactory.getLogger(TemplateRestController.class);

    @Autowired
    private TemplateService templateService;

    @GetMapping("/emailTemplate")
    public ResponseEntity getEmailTemplate(
            @RequestParam(value = "id", required = false) String id
    ) {
        return templateService.getEmailTemplateById(id);
    }

    @PostMapping("/emailTemplate")
    public ResponseEntity createEmailTemplate(@RequestBody String requestBody) {
        return templateService.createEmailTemplate(requestBody);
    }

    @PutMapping("/emailTemplate")
    public ResponseEntity updateEmailTemplate(
            @RequestBody(required = false) String requestBody,
            @RequestParam("id") String id
    ) {
        return templateService.updateEmailTemplate(id, requestBody);
    }

    @DeleteMapping("/emailTemplate")
    public ResponseEntity deleteEmailTemplate(
            @RequestBody(required = false) String requestBody,
            @RequestParam("id") String id
    ) {
        return templateService.deleteEmailTemplate(id);
    }
}
