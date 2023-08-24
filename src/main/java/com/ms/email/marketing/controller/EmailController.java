package com.ms.email.marketing.controller;

import com.ms.email.marketing.constant.AppConstant;
import com.ms.email.marketing.model.EmailLogModel;
import com.ms.email.marketing.repository.EmailLogRepository;
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

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping(value = AppConstant.API_VERSION_V1)
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class EmailController {

    private final Logger log = LoggerFactory.getLogger(EmailController.class);
    @Autowired
    private EmailService emailService;
    @Autowired
    private FileService fileService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private EmailLogRepository emailLogRepository;

    @GetMapping("/tracking-pixel.png")
    public ResponseEntity<?> emailTrack(@RequestParam(value = "id", required = false) String emailID) {
        //<img src="https://your-app-domain.com/tracking-pixel.png?emailId=12345" alt="tracking-pixel">
        log.info("This Email ID just accessed: {}", emailID);
        // Log the email open event or perform desired actions
        try {
            if (emailID != null && !emailID.isEmpty()) {
                Optional<EmailLogModel> emailLogModel = emailLogRepository.findByEmailTrackId(emailID);
                if (emailLogModel.isPresent()) {
                    log.info("Found, will update: "+ emailID);
                    EmailLogModel existEmailLogModel = emailLogModel.get();
                    existEmailLogModel.setReadDateTime(LocalDateTime.now());
                    emailLogRepository.saveAndFlush(existEmailLogModel);
                }
            }
        } catch (Exception e) {
            log.error("Error: " + e);
        }
        // Return a 1x1 transparent pixel image as the response
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(new byte[0], headers, HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file
            , @RequestParam(value = "customerGroupId", required = false) String customerGroupId
    ) throws Exception {
        log.info("upload : " + file.getOriginalFilename());
        return fileService.uploadFile(file, customerGroupId);
    }

}
