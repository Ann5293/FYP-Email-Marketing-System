package com.ms.email.marketing.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ms.email.marketing.constant.AppConstant;
import com.ms.email.marketing.service.CustomerService;
import com.ms.email.marketing.service.EmailService;
import com.ms.email.marketing.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;

@RestController
@RequestMapping(value = AppConstant.API_VERSION_V1)
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class EmailController {

    private final Logger LOG = LoggerFactory.getLogger(EmailController.class);
    @Autowired private EmailService emailService;
    @Autowired private FileService fileService;
    @Autowired private CustomerService customerService;

    @PostMapping("/email")
    public ResponseEntity<?> emailSetup(@RequestBody String emailSetupBody) throws JsonProcessingException {
        return emailService.sendEmail(emailSetupBody);
    }

    @PostMapping("/bulkemail")
    public ResponseEntity bulkEmailSetup(@RequestBody String requestBody) throws MessagingException {
        return emailService.sendBulkEmail(requestBody);
    }

    @GetMapping("/tracking-pixel.png")
    public ResponseEntity<?> emailTrack(@RequestParam(value = "id", required = false) String emailID){
        //<img src="https://your-app-domain.com/tracking-pixel.png?emailId=12345" alt="tracking-pixel">
        LOG.info("This Email ID just accessed: {}", emailID);
        // Log the email open event or perform desired actions
        System.out.println("Email opened for ID: " + emailID);

        // Return a 1x1 transparent pixel image as the response
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(new byte[0], headers, HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file
            ,@RequestParam(value = "customerGroupId", required = false) String customerGroupId
    ) throws IOException {

        LOG.info("upload : "+file.getOriginalFilename());
        return fileService.uploadFile(file, customerGroupId);
    }

    @GetMapping("/upload")
    public ResponseEntity<?> getUploadFile() throws IOException {
        return fileService.getUploadedFiles();
    }

}
