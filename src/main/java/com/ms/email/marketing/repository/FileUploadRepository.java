package com.ms.email.marketing.repository;

import com.ms.email.marketing.model.ApiLoggingModel;
import com.ms.email.marketing.model.FileUploadModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUploadModel, UUID> {

    @Query(nativeQuery = true, value = "SELECT * FROM ems_file_upload WHERE id = ?1 AND STATUS = ?2")
    Optional<FileUploadModel> getFileByIdAndStatus(Long id, String status);
}
