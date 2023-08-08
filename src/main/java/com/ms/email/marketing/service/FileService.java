package com.ms.email.marketing.service;

import com.ms.email.marketing.model.ApiLoggingModel;
import com.ms.email.marketing.model.FileUploadModel;
import com.ms.email.marketing.repository.FileUploadRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FileService {
    private static final Logger log = LoggerFactory.getLogger(FileService.class);
    public static final String DELIMITER = "|";
    public static final String FILE_CUST_LISTING_HEADERS = "NAME,EMAIL";
    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private CustomerService customerService;

    public ResponseEntity uploadFile(MultipartFile file, String customerGroupId) throws IOException {
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Please input a file!");

        String fileName = file.getOriginalFilename();
        String fileExtension = file.getContentType();
        byte[] fileContent = file.getBytes();
        String base64String = Base64.getEncoder().encodeToString(fileContent);

        ApiLoggingModel apiLoggingModel = ApiLoggingModel.builder()
                .http_method(HttpMethod.POST.name())
                .request_datetime(LocalDateTime.now())
                .request_uri("/email")
                .request_body(null)
                .build();

        FileUploadModel fileModel = FileUploadModel.builder()
                .filename(fileName)
                .fileExtension(fileExtension)
                .base64String(base64String)
                .createdTime(LocalDateTime.now())
                .build();
        fileModel = fileUploadRepository.saveAndFlush(fileModel);
        boolean isFileUploaded = fileModel.getId() != null && fileModel.getId() > 0;
        if (isFileUploaded) {
            try {
                uploadCustomerListing(fileModel, customerGroupId);
                fileUploadRepository.saveAndFlush(fileModel);
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }

        apiLoggingModel.setResponse_body(isFileUploaded ? "File uploaded: " + fileName : "Failed");
        apiLoggingModel.setResponse_status(isFileUploaded ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value());
        apiLoggingModel.setResponse_datetime(LocalDateTime.now());
        ApiLoggingModel finalLogging = apiLoggingModel;
        return ResponseEntity.status(finalLogging.getResponse_status())
                .body(new LinkedHashMap() {{
                    put("status", finalLogging.getResponse_status());
                    put("uuid", finalLogging.getUuid());
                    put("message", finalLogging.getResponse_body());
                }});
    }

    public ResponseEntity getUploadedFiles() {
        List<FileUploadModel> fileUploadModelList = fileUploadRepository.findAll();
        log.info("getUploadedFiles - size: "+ fileUploadModelList.size());
        return ResponseEntity.ok(fileUploadModelList);
    }

    public String uploadCustomerListing(FileUploadModel fileUploadModel, String customerGroupId) throws Exception {
        String fileExtension = getFileExtension(fileUploadModel.getFilename());
        fileUploadModel.setStatus("FAILED");
        if (!Arrays.asList("txt", "xlsx", "csv").contains(fileExtension)) {
            throw new Exception("Uploaded file is of unsupported type.");
        }
        try {
            log.info("fileExtension: "+ fileExtension);
            if ("csv".equalsIgnoreCase(fileExtension)) {
                // Handle CSV file
                List<Map<String, String>> data = readCsvOrTxtFromBase64Data(fileUploadModel.getBase64String());
                customerService.uploadCsvAndTxtDataToDB(data, fileUploadModel.getId(), customerGroupId);
                fileUploadModel.setStatus("SUCCESS");
                return "Uploaded file is a CSV.";
            } else if ("xlsx".equalsIgnoreCase(fileExtension)) {
                // Handle XLSX (Excel) file
                return "Uploaded file is an Excel (XLSX) file.";
            } else if ("txt".equalsIgnoreCase(fileExtension)) {
                // Handle text file
                return "Uploaded file is a text file.";
            }
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
