package com.ms.email.marketing.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "ems_customer_listing")
public class CustomerListingModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String name;
    //private String fields;
    private String status;

    @ElementCollection
    @CollectionTable(name = "ems_customer_listing_fields", joinColumns = @JoinColumn(name = "ems_customer_listing_id"))
    @MapKeyColumn(name = "field_key")
    @Column(name = "field_value")
    private Map<String, String> fields;

    private Long customerGroupId;
//    private String fields;
    private Long fileUploadId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdTime;

}
