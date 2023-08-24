package com.ms.email.marketing.service;

import com.ms.email.marketing.constant.AppConstant;
import com.ms.email.marketing.model.*;
import com.ms.email.marketing.model.request.EmailRequest;
import com.ms.email.marketing.repository.*;
import com.ms.email.marketing.utils.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String EMAIL_SENDER;
    @Value("${app.domain.ip}")
    private String APP_DOMAIN_IP;
    @Value("${app.domain.port}")
    private String APP_DOMAIN_PORT;

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;
    @Autowired private CommonUtil commonUtil;
    @Autowired private FileService fileService;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private EmailLogRepository emailLogRepository;
    @Autowired private EmailTemplateRepository emailTemplateRepo;
    @Autowired private CustomerGroupRepository customerGroupRepo;
    @Autowired private CampaignRepository campaignRepo;
    @Autowired private CustomerService customerService;

    public void startBlastCampaign(Long templateId, Long customerGroupId, String campaignName) {
        log.info("startBlastCampaign - customerGroupId: "+ customerGroupId);
        Optional<EmailTemplateModel> template = emailTemplateRepo.findById(templateId);
        Optional<CustomerGroupModel> customerGroup = customerGroupRepo.findById(customerGroupId);

        CampaignModel campaignModel = new CampaignModel();
        campaignModel.setName(campaignName);
        campaignModel.setEmailTemplateId(templateId);
        campaignModel.setCustomerGroupId(customerGroupId);
        campaignModel.setStatus("PENDING");
        campaignModel = campaignRepo.saveAndFlush(campaignModel);

        if (template.isPresent() && customerGroup.isPresent()) {
            EmailTemplateModel templateModel = template.get();
            List<CustomerModel> customerListing = customerRepository.findAllByCustomerGroupIdAndStatusNot(customerGroupId, AppConstant.STATUS_DELETED);
            for(CustomerModel cust : customerListing){
                String emailTrackingID = UUID.randomUUID().toString();
                log.info("trackingID: "+ emailTrackingID);
                EmailRequest emailRequest = new EmailRequest();
                emailRequest.setTo(cust.getEmail());
                emailRequest.setSubject(templateModel.getSubject());
                emailRequest.setType(templateModel.getType());
                emailRequest.setMessage(templateModel.getBody());
                emailRequest.setTrackingUuid(emailTrackingID);
                //Logging prupose
                EmailLogModel emailLoggingModel = new EmailLogModel();
                emailLoggingModel.setEmailTo(emailRequest.getTo());
                emailLoggingModel.setEmailSubject(emailRequest.getSubject());
                emailLoggingModel.setEmailType(emailRequest.getType());
                emailLoggingModel.setEmailTrackId(emailTrackingID);
                String message = emailRequest.getMessage();
                log.info("MESSAGE: "+ message);
                log.info("FIELDS: "+ cust.getFields());

                for (Map.Entry<String, String> set : cust.getFields().entrySet()) {
                    log.info("email: {}, set.getKey: {}", emailRequest.getTo(), set.getKey());
                    if(message.contains(String.format("#%s#", set.getKey().toUpperCase()))){
                        log.info("Found: "+ set.getKey());
                        message = message.replaceAll(String.format("#%s#", set.getKey().toUpperCase()), set.getValue());
                    }
                    // Printing all elements of a Map
                }
                log.info("MESSAGE END: "+ message);
                emailRequest.setMessage(message);
                try {
                    if (emailRequest.getType() != null && emailRequest.getType().equalsIgnoreCase("HTML")) {
                        sendHtmlEmail(emailRequest);
                    } else {
                        sendPlainEmail(emailRequest);
                    }
                    emailLoggingModel.setStatus("SUCCESS");
                }catch (Exception e){
                    log.error("sendBulkEmail error: "+e);
                    emailLoggingModel.setErrorMsg(e.getMessage());
                    emailLoggingModel.setStatus("FAILED");
                }
                try{
                    emailLoggingModel.setCampaignId(campaignModel.getId());
                    emailLogRepository.save(emailLoggingModel);
                }catch (Exception e){
                    log.error("Save email logging failed");
                }
            }
        }
        campaignModel.setStatus("COMPLETED");
        campaignRepo.saveAndFlush(campaignModel);
    }

    private void sendPlainEmail(EmailRequest emailRequest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(EMAIL_SENDER);
        message.setTo(emailRequest.getTo());

        message.setSubject(emailRequest.getSubject());
        message.setText(emailRequest.getMessage());
        mailSender.send(message);
    }

    private void sendHtmlEmail(EmailRequest emailRequest) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);

        mimeMessageHelper.setFrom(EMAIL_SENDER);
        mimeMessageHelper.setTo(emailRequest.getTo());
        mimeMessageHelper.setSubject(emailRequest.getSubject());
        String emailContent = emailRequest.getMessage();

        String embbedEmailTrackID = "<img src=\"" + APP_DOMAIN_IP + ":" + APP_DOMAIN_PORT + "/v1/tracking-pixel.png?id=" + emailRequest.getTrackingUuid() + "\" alt=\"tracking-pixel\">";
        log.info("embbedEmailTrackID: "+embbedEmailTrackID);
        String dynamicContent = emailContent + embbedEmailTrackID;

        mimeMessageHelper.setText(dynamicContent, true);
        //message.setContent(emailRequest.getMessage(), "text/html; charset=utf-8");
        mailSender.send(message);
    }

}
