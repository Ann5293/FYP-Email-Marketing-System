package com.ms.email.marketing.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.email.marketing.constant.AppConstant;
import com.ms.email.marketing.model.*;
import com.ms.email.marketing.model.request.BulkEmailRequest;
import com.ms.email.marketing.model.request.EmailRequest;
import com.ms.email.marketing.repository.*;
import com.ms.email.marketing.utils.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @Autowired private ApiLoggingRepository apiLoggingRepository;
    @Autowired private FileService fileService;
    @Autowired private FileUploadRepository fileUploadRepository;
    @Autowired private CustomerListingRepository customerListingRepository;
    @Autowired private EmailLoggingRepository emailLoggingRepo;
    @Autowired private EmailTemplateRepository emailTemplateRepo;
    @Autowired private CustomerGroupRepository customerGroupRepo;
    @Autowired private CampaignRepository campaignRepo;
    @Autowired private CustomerService customerService;
    @Autowired private LoggingService loggingService;

    public void startBlastCampaign(Long templateId, Long customerGroupId) {
        Optional<EmailTemplateModel> template = emailTemplateRepo.findById(templateId);
        Optional<CustomerGroupModel> customerGroup = customerGroupRepo.findById(customerGroupId);

        CampaignModel campaignModel = new CampaignModel();
        campaignModel.setEmailTemplateId(templateId);
        campaignModel.setCustomerGroupId(customerGroupId);
        campaignModel.setStatus("PENDING");
        campaignModel = campaignRepo.saveAndFlush(campaignModel);

        if (template.isPresent() && customerGroup.isPresent()) {
            EmailTemplateModel templateModel = template.get();
            List<CustomerListingModel> customerListing = customerListingRepository.findAllByCustomerGroupIdAndStatusNot(customerGroupId, AppConstant.STATUS_DELETED);
            for(CustomerListingModel cust : customerListing){
                String emailTrackingID = UUID.randomUUID().toString();
                log.info("trackingID: "+ emailTrackingID);
                EmailRequest emailRequest = new EmailRequest();
                emailRequest.setTo(cust.getEmail());
                emailRequest.setSubject(templateModel.getSubject());
                emailRequest.setType(templateModel.getType());
                emailRequest.setMessage(templateModel.getBody());
                emailRequest.setTrackingUuid(emailTrackingID);
                //Logging prupose
                EmailLoggingModel emailLoggingModel = new EmailLoggingModel();
                emailLoggingModel.setEmailTo(emailRequest.getTo());
                emailLoggingModel.setEmailSubject(emailRequest.getSubject());
                emailLoggingModel.setEmailType(emailRequest.getType());
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
                    emailLoggingModel.setStatus("FAILED");
                }
                try{
                    emailLoggingModel.setCampaignId(campaignModel.getId());
                    emailLoggingRepo.save(emailLoggingModel);
                }catch (Exception e){
                    log.error("Save email logging failed");
                }
            }
            campaignModel.setStatus("COMPLETED");
            campaignRepo.saveAndFlush(campaignModel);
        }
    }
    public ResponseEntity sendBulkEmail(String requestBody) throws MessagingException {
        BulkEmailRequest bulkEmailRequest = (BulkEmailRequest) commonUtil.jsonStringToObjectElseNull(requestBody, BulkEmailRequest.class);

        ApiLoggingModel apiLog = loggingService.logApiRequest(HttpMethod.POST.name(), "/sendBulkEmail", null);

        Optional<FileUploadModel> file = fileUploadRepository.getFileByIdAndStatus(bulkEmailRequest.getCustomerListingId(), "Success");
        String errorMsg = "";
        if (!file.isPresent()) {
            errorMsg = "File not found: " + bulkEmailRequest.getCustomerListingId();
        }
        if (errorMsg.isEmpty()) {
            List<CustomerListingModel> customerListingModels = customerListingRepository.findAllByFileUploadId(bulkEmailRequest.getCustomerListingId());
            log.info("customerListingModels: "+customerListingModels.size());
            for(CustomerListingModel cust : customerListingModels){
                String emailTrackingID = UUID.randomUUID().toString();
                log.info("trackingID: "+ emailTrackingID);
                EmailRequest emailRequest = new EmailRequest();
                emailRequest.setTo(cust.getEmail());
                emailRequest.setSubject(bulkEmailRequest.getSubject());
                emailRequest.setType(bulkEmailRequest.getType());
                emailRequest.setMessage(bulkEmailRequest.getMessage());
                emailRequest.setTrackingUuid(emailTrackingID);
                EmailLoggingModel emailLoggingModel = new EmailLoggingModel();
                emailLoggingModel.setEmailTo(emailRequest.getTo());
                emailLoggingModel.setEmailSubject(emailRequest.getSubject());
                //emailLoggingModel.set(file.get().getId());
                String message = emailRequest.getMessage();
                log.info("MESSAGE: "+ message);
                log.info("FIELDS: "+ cust.getFields());
                for (Map.Entry<String, String> set : cust.getFields().entrySet()) {
                    if(message.contains(String.format("#%s#", set.getKey().toUpperCase()))){
                        message = message.replaceAll(String.format("#%s#", set.getKey().toUpperCase()), set.getValue());
                    }
                    // Printing all elements of a Map
                    //System.out.println(set.getKey() + " = " + set.getValue());
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
                    emailLoggingModel.setStatus("FAILED");
                }
                try{
                    emailLoggingRepo.save(emailLoggingModel);
                }catch (Exception e){
                    log.error("Save email logging failed");
                }
            }
        }

        apiLog.setResponse_body(errorMsg.isEmpty() ? "Triggered" : errorMsg);
        apiLog.setResponse_status(errorMsg.isEmpty() ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value());
        //apiLoggingRepository.saveAndFlush(apiLog);
        apiLoggingRepository.saveAndFlush(apiLog);
        ApiLoggingModel finalApiLog = apiLog;
        return ResponseEntity.status(finalApiLog.getResponse_status())
                .body(new LinkedHashMap() {{
                    put("status", finalApiLog.getResponse_status());
                    put("uuid", finalApiLog.getUuid());
                    put("message", finalApiLog.getResponse_body());
                }});
    }

    public ResponseEntity sendEmail(String emailRequestBody) throws JsonProcessingException {
        //1. Validate the request to object mapping
        EmailRequest emailRequest = (EmailRequest) commonUtil.jsonStringToObjectElseNull(emailRequestBody, EmailRequest.class);

        ApiLoggingModel apiLog = ApiLoggingModel.builder()
                .http_method(HttpMethod.POST.name())
                .request_uri("/email")
                .request_body(emailRequestBody)
                .build();
        apiLog = apiLoggingRepository.saveAndFlush(apiLog);
        //3. Save error response and return;
        if (emailRequest == null) {
            apiLog.setResponse_status(HttpStatus.INTERNAL_SERVER_ERROR.value());
            apiLog.setResponse_body("Invalid Request body");
        }
        if (apiLog.getUuid() == null) {
            apiLog.setResponse_status(HttpStatus.INTERNAL_SERVER_ERROR.value());
            apiLog.setResponse_body("Unable save request to body");
        }

        if (apiLog != null && apiLog.getUuid() != null && emailRequest != null) {
            try {
                validateEmailRequest(emailRequest);
                if (emailRequest.getType() != null && emailRequest.getType().equalsIgnoreCase("HTML")) {
                    sendHtmlEmail(emailRequest);
                } else {
                    sendPlainEmail(emailRequest);
                }
                apiLog.setResponse_body("Email Sent Successfully");
                apiLog.setResponse_status(HttpStatus.OK.value());
                log.info("Email Sent Successfully: {}", commonUtil.printString(apiLog));
            } catch (Exception e) {
                log.error("sendEmail error: {}", e);
                apiLog.setResponse_body("Send email hit error: ");
                apiLog.setResponse_status(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }

        apiLoggingRepository.saveAndFlush(apiLog);
        ApiLoggingModel finalApiLog = apiLog;
        return ResponseEntity.status(finalApiLog.getResponse_status())
                .body(new LinkedHashMap() {{
                    put("status", finalApiLog.getResponse_status());
                    put("uuid", finalApiLog.getUuid());
                    put("message", finalApiLog.getResponse_body());
                }});
    }


    private void validateEmailRequest(EmailRequest emailRequest) throws Exception {
        if (emailRequest == null) throw new Exception("Request is null");
        if (StringUtils.isEmpty(emailRequest.getTo())) throw new Exception("Request emailTo is empty");
        if (StringUtils.isEmpty(emailRequest.getSubject())) throw new Exception("Request subject is empty");
        if (StringUtils.isEmpty(emailRequest.getMessage())) throw new Exception("Request message is empty");
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
