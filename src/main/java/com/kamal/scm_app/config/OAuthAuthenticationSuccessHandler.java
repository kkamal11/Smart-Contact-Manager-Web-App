package com.kamal.scm_app.config;

import com.kamal.scm_app.service.email.EmailService;
import com.kamal.scm_app.utils.AppConstants;
import com.kamal.scm_app.models.Providers;
import com.kamal.scm_app.models.User;
import com.kamal.scm_app.repository.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final Logger logger = LoggerFactory.getLogger(OAuthAuthenticationSuccessHandler.class);

    @Autowired
    private UserRepo userRepo;

    private final BCryptPasswordEncoder passwordEncoder;

    public OAuthAuthenticationSuccessHandler(BCryptPasswordEncoder encoder){
        this.passwordEncoder = encoder;
    }

    @Autowired
    private EmailService emailService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        logger.info("{} ==> Just Entered onAuthenticationSuccess", this.getClass());

        //identify the provider
        var oAuthAuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String authorizedClientRegistrationId = oAuthAuthenticationToken.getAuthorizedClientRegistrationId();

        logger.info("{} ==>Received ClientId {}",this.getClass(), authorizedClientRegistrationId);

        //save data to db once user signs in
        DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();
/*
        user.getAttributes().forEach((key, val) -> {
            logger.info("{} => {}", key, val);
        });
        logger.info(user.getAttributes().toString());
*/

        User userToBeSaved = new User();
        userToBeSaved.setUserId(UUID.randomUUID().toString());
        userToBeSaved.setPassword(passwordEncoder.encode(AppConstants.OAUTH_DEFAULT_PASSWORD));
        userToBeSaved.setEnabled(true);
        userToBeSaved.setEmailVerified(true);
        userToBeSaved.setRoleList(List.of(AppConstants.ROLE_USER));

        LocalDate currentDate = LocalDate.now();
        userToBeSaved.setJoinedOn(currentDate);

        String email = null;

        if(authorizedClientRegistrationId.equalsIgnoreCase("google")) {

            String name = user.getAttribute("name").toString();
            email = user.getAttribute("email").toString();
            String profilePicLink = user.getAttribute("picture").toString();
            userToBeSaved.setEmail(email);
            userToBeSaved.setName(name);
            userToBeSaved.setProfilePicLink(profilePicLink);
            userToBeSaved.setProvider(Providers.GOOGLE);
            userToBeSaved.setProviderUserId(user.getName());
            userToBeSaved.setAbout("Your Account was created using Google.");
        } else if (authorizedClientRegistrationId.equalsIgnoreCase("github")) {

            String name = user.getAttribute("name")!= null ? user.getAttribute("name").toString()
                    : user.getAttribute("login").toString();
            email = user.getAttribute("email") != null ? user.getAttribute("email").toString()
                    : user.getAttribute("login") + "@dummy.com";
            String profilePicLink = user.getAttribute("avatar_url").toString();
            userToBeSaved.setEmail(email);
            userToBeSaved.setName(name);
            userToBeSaved.setProfilePicLink(profilePicLink);
            userToBeSaved.setProvider(Providers.GITHUB);
            userToBeSaved.setProviderUserId(user.getName());
            userToBeSaved.setAbout("Your Account was created using Github.");
            
        } else if (authorizedClientRegistrationId.equalsIgnoreCase("linkedin")) {
            logger.info("LinkedIn Impl");
        }
        else {
            logger.info("Unknown Provider");
        }

        User userOptionalFromDb = userRepo.findByEmail(email).orElse(new User());

        if (userOptionalFromDb.getEmail() == null) {
            userRepo.save(userToBeSaved);
            try {
                String htmlContent = String.format(
                        AppConstants.htmlEmailTemplate,
                        userToBeSaved.getName(),
                        "Since you have used third party to login, we have set a default password as below for you.",
                        "Default password: <b>password</b>",
                        "Please change it for security purpose.");
                emailService.sendEmailWithHtml(
                        htmlContent,
                        userToBeSaved.getEmail(),
                        "Change your default Password"
                );
            }
            catch (MessagingException mEx){
                System.out.println(mEx.getMessage());
            }
            logger.info("OAuthAuthenticationSuccessHandler ==> User saved with email {}", email);
        } else {
            logger.info("OAuthAuthenticationSuccessHandler ==> User already exists with email {}", email);
        }
        new DefaultRedirectStrategy().sendRedirect(request, response,AppConstants.POST_LOGIN_REDIRECT_URL);
    }
}
