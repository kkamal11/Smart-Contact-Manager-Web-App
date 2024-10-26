package com.kamal.scm_app.config;

import com.kamal.scm_app.utils.alert_message.Message;
import com.kamal.scm_app.utils.alert_message.MessageType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuthFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        logger.info("In {} ==> class", this.getClass());
        if(exception instanceof DisabledException){
            //user is disabled
            HttpSession session = request.getSession();
            Message message = new Message("User is disabled. Verification Link was sent to your email. Please verify", MessageType.red);
            session.setAttribute("message", message);
            response.sendRedirect("/login");
        }
        else {
            response.sendRedirect("/login?error=true");
        }
    }

}
