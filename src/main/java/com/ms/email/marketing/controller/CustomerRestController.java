package com.ms.email.marketing.controller;

import com.ms.email.marketing.constant.AppConstant;
import com.ms.email.marketing.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequestMapping(value = AppConstant.API_VERSION_V1)
public class CustomerRestController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/customerGroup")
    public ResponseEntity getAllCustomerGroup(){
        return ResponseEntity.ok(customerService.getAllCustomerGroup());
    }

    @PostMapping("/customerGroup")
    public ResponseEntity createCustomerGroup(
            @RequestBody String requestBody
    ){
        return customerService.createNewCustomerGroup(requestBody);
    }

    @PutMapping("/customerGroup/{groupId}")
    public ResponseEntity updateCustomerGroup(
            @RequestBody(required = false) String requestBody
            ,@PathVariable("groupId") String groupId
    ){
        return customerService.updateCustomerGroup(requestBody, groupId);
    }
    @DeleteMapping("/customerGroup/{groupId}")
    public ResponseEntity deleteCustomerGroup(
            @RequestBody(required = false) String requestBody
            ,@PathVariable("groupId") String groupId
    ){
        return customerService.deleteCustomerGroup(groupId);
    }
}
