package com.ms.email.marketing.constant;

import org.springframework.stereotype.Component;

@Component
public class AppConstant {

    public static final String API_VERSION_V1 = "/v1";
    public static final String API_BASE = "/test";
    public static final String API_REGISTER = "/register";
    public static final String API_LOGIN = "/login";
    public static final String API_LOGOUT = "/logout";

    public static final String FLASH_ATTR_ERRORMSG = "errorMsg";
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_DELETED = "DELETED";

    /*
    Campaign
    EmailTemplate
        /Search
        /Create
        /View/Edit
    ClientCategory
    ClientSub
    Settings
     */
}
