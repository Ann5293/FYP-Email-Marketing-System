package com.ms.email.marketing.service;

import com.ms.email.marketing.constant.AppConstant;
import com.ms.email.marketing.model.CustomerGroupModel;
import com.ms.email.marketing.model.CustomerModel;
import com.ms.email.marketing.repository.CustomerGroupRepository;
import com.ms.email.marketing.repository.CustomerRepository;
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
    private CustomerRepository customerRepository;

    @Autowired
    private CommonUtil commonUtil;

    public List<CustomerGroupModel> getAllCustomerGroup() {
        return customerGroupRepository.findAllByStatus(AppConstant.STATUS_ACTIVE);
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
            } else {
                throw new Exception("Customer Group Not found");
            }
        } catch (Exception e) {
            log.error("Error: {}", e);
            errorMsg = e.getMessage();
        }
        String finalErrorMsg = errorMsg;
        return ResponseEntity.status(500)
                .body(new LinkedHashMap() {{
                    put("status", "Failed");
                    put("uuid", UUID.randomUUID());
                    put("message", finalErrorMsg);
                }});
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
                        put("message", "CustomerGroup Id not found!");
                    }});
        }
    }

    public void uploadCsvAndTxtDataToDB(List<Map<String, String>> data, String customerGroupId) {
        List<CustomerModel> customerList = new ArrayList<>();
        for (Map<String, String> rowData : data) {
            CustomerModel cust = new CustomerModel();
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
            cust.setStatus(AppConstant.STATUS_ACTIVE);
            cust.setCustomerGroupId(Long.valueOf(customerGroupId));
            customerList.add(cust);
        }
        for(CustomerModel cust : customerList){
            CustomerModel existCust = customerRepository.findByEmailAndCustomerGroupIdAndStatus(cust.getEmail(), Long.valueOf(customerGroupId), AppConstant.STATUS_ACTIVE);
            if(existCust != null) {
                existCust.setName(cust.getName());
                for (Map.Entry<String, String> entry : cust.getFields().entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    Map<String, String> existFields = existCust.getFields();
                    if (existFields.containsKey(key)) {
                        existFields.put(key, value);
                    } else {
                        existFields.put(key, value);
                    }
                }
                customerRepository.save(existCust);
            }else {
                customerRepository.save(cust);
            }
        }
    }

    public CustomerGroupModel getCustomerGroupById(Long id) {
        return customerGroupRepository.findById(id).orElse(null);
    }

    public ResponseEntity getCustomerListFromCustomerGroup(String groupId) {
        String errorMsg = "";
        try {
            Optional<CustomerGroupModel> customerGroup = customerGroupRepository.findById(Long.valueOf(groupId));
            if (customerGroup.isPresent()) {
                List<CustomerModel> customerList = customerRepository.findAllByCustomerGroupIdAndStatusNot(Long.valueOf(groupId), AppConstant.STATUS_DELETED);
                return ResponseEntity.ok(customerList);
            } else {
                throw new Exception("Customer Group Id not found!");
            }
        } catch (Exception e) {
            log.error("Error: " + e);
            errorMsg = e.getMessage();
        }
        return ResponseEntity.status(500)
                .body(new LinkedHashMap() {{
                    put("status", "Failed");
                    put("uuid", UUID.randomUUID());
                    put("message", "Get failed");
                }});
    }

    public ResponseEntity getCustomerFromCustomerGroup(String groupId, String customerId) {
        String errorMsg = "";
        try {
            Optional<CustomerGroupModel> customerGroup = customerGroupRepository.findById(Long.valueOf(groupId));
            if (!customerGroup.isPresent())
                throw new Exception("Customer Group Id not found!");

            CustomerModel customer = customerRepository.findByIdAndCustomerGroupIdAndStatusNot(Long.valueOf(customerId), Long.valueOf(groupId), AppConstant.STATUS_DELETED);
            if (customer == null)
                throw new Exception("Customer Group Id not found!");
            return ResponseEntity.status(200).body(customer);

        } catch (Exception e) {
            log.error("Error: " + e);
            errorMsg = e.getMessage();
        }
        return ResponseEntity.status(500)
                .body(new LinkedHashMap() {{
                    put("status", "Failed");
                    put("uuid", UUID.randomUUID());
                    put("message", "Get failed");
                }});
    }

    public ResponseEntity createCustomerFromCustomerGroup(String groupId, String requestBody) {
        try {
            CustomerGroupModel customerGroup = customerGroupRepository.findByIdAndStatusNot(Long.valueOf(groupId), AppConstant.STATUS_DELETED);
            if (customerGroup == null)
                throw new Exception("Customer Group Id not found!");
            CustomerModel newCustomer = (CustomerModel) commonUtil.jsonStringToObjectElseNull(requestBody, CustomerModel.class);
            newCustomer.setStatus(AppConstant.STATUS_ACTIVE);
            newCustomer.setCustomerGroupId(Long.valueOf(groupId));
            newCustomer = customerRepository.saveAndFlush(newCustomer);
            return ResponseEntity.status(200).body(newCustomer);
        } catch (Exception e) {
            log.error("Error: " + e);
        }
        return ResponseEntity.status(500)
                .body(new LinkedHashMap() {{
                    put("status", "Failed");
                    put("uuid", UUID.randomUUID());
                    put("message", "Get failed");
                }});
    }

    public ResponseEntity updateCustomerFromCustomerGroup(String groupId, String customerId, String requestBody) {
        String errorMsg = "";
        try {
            CustomerGroupModel customerGroup = customerGroupRepository.findByIdAndStatusNot(Long.valueOf(groupId), AppConstant.STATUS_DELETED);
            if (customerGroup == null)
                throw new Exception("Customer Group Id not found!");
            CustomerModel existCustomer = customerRepository.findByIdAndCustomerGroupIdAndStatusNot(Long.valueOf(customerId), Long.valueOf(groupId), AppConstant.STATUS_DELETED);
            if (existCustomer == null)
                throw new Exception("Customer not found!");
            CustomerModel updateCustomer = (CustomerModel) commonUtil.jsonStringToObjectElseNull(requestBody, CustomerModel.class);
            if (updateCustomer == null)
                throw new Exception("Invalid requestBody");
            existCustomer.setName(updateCustomer.getName());
            existCustomer.setEmail(updateCustomer.getEmail());
            existCustomer.setFields(updateCustomer.getFields());
            existCustomer = customerRepository.saveAndFlush(existCustomer);
            return ResponseEntity.status(200).body(existCustomer);
        } catch (Exception e) {
            log.error("Error: " + e);
            errorMsg = e.getMessage();
        }
        String finalErrorMsg = errorMsg;
        return ResponseEntity.status(errorMsg.isEmpty() ? 200 : 500)
                .body(new LinkedHashMap() {{
                    put("status", finalErrorMsg.isEmpty() ? "Success" : "Failed");
                    put("uuid", UUID.randomUUID());
                    put("message", finalErrorMsg);
                }});
    }

    public ResponseEntity deleteCustomerFromCustomerGroup(String groupId, String customerId) {
        String errorMsg = "";
        try {
            CustomerGroupModel customerGroup = customerGroupRepository.findByIdAndStatusNot(Long.valueOf(groupId), AppConstant.STATUS_DELETED);
            if (customerGroup == null)
                throw new Exception("Customer Group Id not found!");
            CustomerModel existCustomer = customerRepository.findByIdAndCustomerGroupIdAndStatusNot(Long.valueOf(customerId), Long.valueOf(groupId), AppConstant.STATUS_DELETED);
            if (existCustomer == null)
                throw new Exception("Customer not found!");
            existCustomer.setStatus(AppConstant.STATUS_DELETED);
            existCustomer = customerRepository.saveAndFlush(existCustomer);
            return ResponseEntity.status(200).body(existCustomer);
        } catch (Exception e) {
            log.error("Error: " + e);
            errorMsg = e.getMessage();
        }
        String finalErrorMsg = errorMsg;
        return ResponseEntity.status(errorMsg.isEmpty() ? 200 : 500)
                .body(new LinkedHashMap() {{
                    put("status", finalErrorMsg.isEmpty() ? "Success" : "Failed");
                    put("uuid", UUID.randomUUID());
                    put("message", finalErrorMsg);
                }});
    }

}
