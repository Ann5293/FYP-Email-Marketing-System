package com.ms.email.marketing.service;

import com.ms.email.marketing.constant.AppConstant;
import com.ms.email.marketing.model.CustomerGroupModel;
import com.ms.email.marketing.model.CustomerListingModel;
import com.ms.email.marketing.repository.CustomerGroupRepository;
import com.ms.email.marketing.repository.CustomerListingRepository;
import com.ms.email.marketing.utils.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
    @Autowired
    private CustomerGroupRepository customerGroupRepository;
    @Autowired
    private CustomerListingRepository customerListingRepository;

    @Autowired
    private CommonUtil commonUtil;

    public List<CustomerGroupModel> getAllCustomerGroup() {
        //return customerGroupRepository.findAll();
        return customerGroupRepository.findAllByStatusNot(AppConstant.STATUS_DELETED);
    }

    public ResponseEntity createNewCustomerGroup(String requestBody) {
        String errorMsg = "";

        CustomerGroupModel customerGroupModel = null;
        try {
            customerGroupModel = (CustomerGroupModel) commonUtil.jsonStringToObjectElseNull(requestBody, CustomerGroupModel.class);
            if (customerGroupModel == null) {
                throw new Exception("Unable to create the customer group!");
            }
            customerGroupModel.setStatus(AppConstant.STATUS_ACTIVE);
            customerGroupModel = customerGroupRepository.save(customerGroupModel);

        } catch (Exception e) {
            log.error("Error: " + e);
            errorMsg = e.getMessage();
        }
        if (errorMsg.isEmpty()) {
            return ResponseEntity.status(200).body(customerGroupModel);
        }
        return ResponseEntity.status(500).body(errorMsg);
    }

    public ResponseEntity updateCustomerGroup(String requestBody, String customerGroupId) {
        String errorMsg = "";
        try {
            CustomerGroupModel customGroupEdit = (CustomerGroupModel) commonUtil.jsonStringToObjectElseNull(requestBody, CustomerGroupModel.class);
            Optional<CustomerGroupModel> customerGroupModel = customerGroupRepository.findById(Long.valueOf(customerGroupId));
            if (customerGroupModel.isPresent()) {
                CustomerGroupModel existCustomer = customerGroupModel.get();
                existCustomer.setDescription(customGroupEdit.getDescription());
                existCustomer.setName(customGroupEdit.getName());
                customerGroupRepository.save(existCustomer);
                return ResponseEntity.status(200)
                        .body(new LinkedHashMap() {{
                            put("status", "Success");
                            put("uuid", UUID.randomUUID());
                            put("message", "Success updated.");
                        }});
            }
        } catch (Exception e) {
            log.error("Error: {}", e);
            errorMsg = e.getMessage();
        }
        return ResponseEntity.status(500).body(errorMsg);
    }

    public ResponseEntity deleteCustomerGroup(String customerGroupId) {
        Optional<CustomerGroupModel> customerGroupModel = customerGroupRepository.findById(Long.valueOf(customerGroupId));
        if (customerGroupModel.isPresent()) {
            CustomerGroupModel existCustGroup = customerGroupModel.get();
            existCustGroup.setStatus(AppConstant.STATUS_DELETED);
            customerGroupRepository.saveAndFlush(existCustGroup);
            return ResponseEntity.status(200)
                    .body(new LinkedHashMap() {{
                        put("status", "Success");
                        put("uuid", UUID.randomUUID());
                        put("message", "Success deleted.");
                    }});
        } else {
            return ResponseEntity.status(500)
                    .body(new LinkedHashMap() {{
                        put("status", "Failed");
                        put("uuid", UUID.randomUUID());
                        put("message", "Delete failed");
                    }});
        }
    }

    public void uploadCsvAndTxtDataToDB(List<Map<String, String>> data, Long fileUploadID, String customerGroupId) {
        List<CustomerListingModel> customerList = new ArrayList<>();
        for (Map<String, String> rowData : data) {
            CustomerListingModel cust = new CustomerListingModel();
            cust.setFileUploadId(fileUploadID);
            Map<String, String> fields = new HashMap<>();
            for (Map.Entry<String, String> entry : rowData.entrySet()) {
                String header = entry.getKey();
                String value = entry.getValue();
                log.info(header + ": " + value);
                if ("NAME".equalsIgnoreCase(header)) {
                    cust.setName(value);
                } else if ("EMAIL".equalsIgnoreCase(header) || header.contains("EMAIL")) {
                    cust.setEmail(value);
                } else {
                    log.info("customAttribute: [{}]", header);
                    //﻿EMAIL
                    fields.put(header, value);
                }
            }
            cust.setFields(fields);
            cust.setCustomerGroupId(Long.valueOf(customerGroupId));
            customerList.add(cust);
        }
        customerListingRepository.saveAll(customerList);
    }

    public CustomerGroupModel getCustomerGroupById(Long id){
        return customerGroupRepository.findById(id).orElse(null);
    }
}
