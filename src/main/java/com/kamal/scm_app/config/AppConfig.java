package com.kamal.scm_app.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfig {

    @Value("${CLOUDINARY_URL}") //reading from application.properties
    private String CLOUDINARY_URL;

    @Bean
    public Cloudinary cloudinary(){
        return new Cloudinary(CLOUDINARY_URL);
    }

    @Bean
    public BCryptPasswordEncoder ScmPasswordEncoder(){
        return new BCryptPasswordEncoder(12);
    }
}
