package com.kamal.scm_app.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;


public final class Utility {
    private static final Logger logger = LoggerFactory.getLogger(Utility.class);

    public static String getEmailOfLoggedInUser(Authentication authentication){
        String email = authentication.getName();
        if(authentication instanceof OAuth2AuthenticationToken oAuthAuthenticationToken){
            String registrationClient = oAuthAuthenticationToken.getAuthorizedClientRegistrationId();

            var oAuth2User = (OAuth2User) authentication.getPrincipal();

            if(registrationClient.equalsIgnoreCase("google")){
                logger.info("User logged in via Google");
                email = oAuth2User.getAttribute("email").toString();

            } else if (registrationClient.equalsIgnoreCase("github")) {
                logger.info("User logged in via Github");
                email = oAuth2User.getAttribute("email") != null ? oAuth2User.getAttribute("email").toString()
                        : oAuth2User.getAttribute("login") + "@dummy.com";
            }
            else{
                logger.info("Unknown Provider");
            }
        }
        else{
            email = email;
        }
        return email;
    }

    public static String getLinkForEmailVerification(String emailToken){
        String link = AppConstants.APP_BASE_URL + "auth/verify-email?token=" + emailToken;
        return link;
    }
}
