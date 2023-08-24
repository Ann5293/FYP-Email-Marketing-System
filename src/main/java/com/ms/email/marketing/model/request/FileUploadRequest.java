package com.ms.email.marketing.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadRequest {
    private String fileName;
    private String fileExtension;
    private String base64String;

}
