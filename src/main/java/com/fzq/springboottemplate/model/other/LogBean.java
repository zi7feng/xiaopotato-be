package com.fzq.springboottemplate.model.other;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * log bean
 */
@Document(collection = "service-logs")
@Data
public class LogBean {
    private String id;
    private Integer userId;
    private String username;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date createDate;
    private String ip;
    private String className; // Class Name
    private String method; // Method Name
    private String reqParam; // Request Parameter
}
