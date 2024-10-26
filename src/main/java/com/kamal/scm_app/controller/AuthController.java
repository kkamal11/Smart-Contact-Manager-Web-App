package com.kamal.scm_app.controller;

import com.kamal.scm_app.models.User;
import com.kamal.scm_app.service.user.UserService;
import com.kamal.scm_app.utils.alert_message.Message;
import com.kamal.scm_app.utils.alert_message.MessageType;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private UserService userService;

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token, HttpSession session){
        User user = userService.getUserByToken(token);
        if(user != null){
            logger.info("AuthController ==> User found with token {}",token);
            if(user.getEmailVerificationToken().equals(token) &&  !user.isEnabled()){
                user.setEmailVerified(true);
                user.setEnabled(true);
                User savedUser = userService.updateUser(user).get();
                logger.info("AuthController ==> User verified with email {}",savedUser.getEmail());

                Message message = new Message("Account Successfully Verified. You can login Now.", MessageType.green);
                session.setAttribute("message",message);

                return "redirect:/login";

            } else if (user.getEmailVerificationToken().equals(token) &&  user.isEnabled()) {
                Message message = new Message("Account Already Verified.", MessageType.green);
                session.setAttribute("message",message);
                return "redirect:/login";

            }
            return "error_page";
        }
        return "error_page";
    }
}
