package com.kamal.scm_app.controller;

import com.kamal.scm_app.utils.Utility;
import com.kamal.scm_app.models.User;
import com.kamal.scm_app.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/*
 * All methods present in this controller will get invoked for all routes
 * and requests present in this application.
 */
@ControllerAdvice
public class RootController {

    private final Logger logger = LoggerFactory.getLogger(RootController.class);

    @Autowired
    private UserService userService;

    @ModelAttribute //means that it gets invoked on every url route of this controller
    public void addLoggedInUserInformation(Model model, Authentication authentication){
        if(authentication == null){
            return;
        }
        String email = Utility.getEmailOfLoggedInUser(authentication);
        User loggedInUser = userService.getUserByEmail(email);
        logger.info("{}==> LoggedIn User is {} with roles {}",this.getClass(), email,loggedInUser.getAuthorities());
        model.addAttribute("loggedInUser", loggedInUser);
    }
}
