package com.ms.email.marketing.service;

import com.ms.email.marketing.model.response.CampaignResultResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class FileService {
    private static final Logger log = LoggerFactory.getLogger(FileService.class);
    public static final String DELIMITER = "|";
    public static final String FILE_CUST_LISTING_HEADERS = "NAME,EMAIL";
    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";

    @Autowired
    private CustomerService customerService;

    public ResponseEntity uploadFile(MultipartFile file, String customerGroupId) throws Exception {
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Please input a file!");
//        String fileName = file.getOriginalFilename();
//        String fileExtension = file.getContentType();
//        byte[] fileContent = file.getBytes();
//        String base64String = Base64.getEncoder().encodeToString(fileContent);
        uploadCustomerListing(file, customerGroupId);

        return ResponseEntity.status(200)
                .body(new LinkedHashMap() {{
                    put("status", "SUCCESS");
                    put("message", "Success Message");
                }});
    }

    public byte[] generateCampaignResultCsvFile(List<CampaignResultResponse> campaignResultResponses) {
        StringBuilder csvContent = new StringBuilder("email, status, errorMsg, sentDateTime\n");
        for (CampaignResultResponse customer : campaignResultResponses) {
            csvContent
                    .append(customer.getEmail() == null ? "" : customer.getEmail()).append(",")
                    .append(customer.getEmailStatus()).append(",")
                    .append(customer.getErrorMsg() == null ? "" : customer.getErrorMsg()).append(",")
                    .append(customer.getSentDateTime())
                    .append("\n");
        }
        return csvContent.toString().getBytes();
    }

    public String uploadCustomerListing(MultipartFile file, String customerGroupId) throws Exception {
        String fileExtension = getFileExtension(file.getOriginalFilename());
//        if (!Arrays.asList("txt", "xlsx", "csv").contains(fileExtension)) {
        if(!"csv".contains(fileExtension))
            throw new Exception("Uploaded file is not csv format.");

        try {
            log.info("fileExtension: "+ fileExtension);
            if ("csv".equalsIgnoreCase(fileExtension)) {
                // Handle CSV file
                byte[] fileContent = file.getBytes();
                String base64String = Base64.getEncoder().encodeToString(fileContent);

                List<Map<String, String>> data = readCsvOrTxtFromBase64Data(base64String);
                customerService.uploadCsvAndTxtDataToDB(data, customerGroupId);
                return "Uploaded file is a CSV.";
            }
//            else if ("xlsx".equalsIgnoreCase(fileExtension)) {
//                // Handle XLSX (Excel) file
//                return "Uploaded file is an Excel (XLSX) file.";
//            } else if ("txt".equalsIgnoreCase(fileExtension)) {
//                // Handle text file
//                return "Uploaded file is a text file.";
//            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("uploadCustomerListing: {}", e);
        }
        return null;
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.lastIndexOf(".") != -1) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        }
        return "";
    }

    private List<Map<String, String>> readCsvOrTxtFromBase64Data(String base64String) throws IOException {
        byte[] base64decoderContent = Base64.getDecoder().decode(base64String);
        List<Map<String, String>> data = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(base64decoderContent)));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord csvRecord : csvParser) {
                Map<String, String> rowData = new HashMap<>();

                for (String headerName : csvParser.getHeaderMap().keySet()) {
                    log.info("header:{} -> value:{}", headerName, csvRecord.get(headerName));
                    rowData.put(headerName, csvRecord.get(headerName));
                }
                data.add(rowData);
            }
        }
        return data;
    }

}
