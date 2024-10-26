package com.kamal.scm_app.utils;

import org.springframework.beans.factory.annotation.Value;


public final class AppConstants {
    public static final String APP_NAME = "SCM";
    public static final String VERSION_NO = "v1";
    @Value("${server.port}")
    public static String APP_PORT;

    public static final String APP_ADMIN_EMAIL = "dev.kamal.kishor@gmail.com";

    public static final String APP_BASE_URL = "http://localhost:8080/";

    public static final String POST_LOGIN_REDIRECT_URL = "/user/dashboard";

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_EDITOR = "ROLE_EDITOR";

    //those signing using third party like Google has default password - password
    public static final String OAUTH_DEFAULT_PASSWORD = "password";

    public static final Integer CONTACT_IMAGE_WIDTH = 500;
    public static final Integer CONTACT_IMAGE_HEIGHT = 500;
    public static final String CONTACT_IMAGE_CROP = "fill";


    //validator constants
    public static final Long MAX_FILE_SIZE = 1024 * 1024 * 2L; //2 MB


    public static final String PAGE_SIZE = "5";

    public static String htmlEmailTemplate = "Hi %s, <br /><br />" +
            "Welcome to <b>SCM - by Kamal</b>.<br /><br />" +
            "<p>%s.</p>" +  //message
            "<p>%s.</p><br />"+  //message
            "%s" + //message
            "<br /><br /><br /><br />With regards,<br />" +
            "<p color='blue'>Kamal Kishor</p>";
}
