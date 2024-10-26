package com.kamal.scm_app.controller;

import com.kamal.scm_app.models.AdminAccessReq;
import com.kamal.scm_app.models.User;
import com.kamal.scm_app.service.AdminAccessReqService;
import com.kamal.scm_app.service.RoleService;
import com.kamal.scm_app.service.email.EmailService;
import com.kamal.scm_app.service.user.UserService;
import com.kamal.scm_app.utils.AppConstants;
import com.kamal.scm_app.utils.Utility;
import com.kamal.scm_app.utils.alert_message.Message;
import com.kamal.scm_app.utils.alert_message.MessageType;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AdminAccessReqService adminAccessReqService;

    @GetMapping("/dashboard")
    public String redirectToUserDashboard() {
        return "redirect:/user/dashboard";  // Redirect to /user/dashboard
    }

    @GetMapping("/req")
    public String requestAdminRole(HttpSession session, Authentication authentication) {
        String email = Utility.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        Message message;

        AdminAccessReq isReqAlreadyRaised = adminAccessReqService.getByUser(user);
        logger.info("In {} inside requestAdminRole method.", this.getClass());
        if (isReqAlreadyRaised == null) { //if there is record means req has been raised
            AdminAccessReq req = new AdminAccessReq();
            req.setEmail(email);
            req.setUser(user);
            req.setPending(true);
            AdminAccessReq savedReq = adminAccessReqService.saveRequest(req);

            try {
                String htmlContent = String.format(
                        AppConstants.htmlEmailTemplate,
                        "Kamal",
                        "The below user is requesting for admin access",
                        "User email: " + user.getEmail(),
                        "Please do the needful.");
                emailService.sendEmailWithHtml(
                        htmlContent,
                        AppConstants.APP_ADMIN_EMAIL,
                        "Admin access request"
                );
                message = new Message("Admin has been notified for the same request. Wait for approval", MessageType.green);
            } catch (Exception e) {
                message = new Message("Something went wrong.", MessageType.red);
                e.getMessage();
            }
        } else {
            message = new Message("Request has already been raised. Please wait for approval", MessageType.red);
        }

        session.setAttribute("message", message);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/give-access")
    public String getAdminAccess(
            @RequestParam(value = "selectedEmails", required = false) List<String> selectedEmails,
            @RequestParam("action") String action,
            HttpSession session,
            Model model) {
        Message message = null;
        if(selectedEmails == null || selectedEmails.isEmpty()){
            message = new Message("No email is selected. Please select at least one.", MessageType.red);
            session.setAttribute("message", message);
            return "redirect:/admin/dashboard";
        }
        logger.info("In {} getAdminAccess ==> Just entered. Selected emails are {}",this.getClass(),selectedEmails);

        if ("accept".equals(action)) {
            logger.info("In {} Accepted emails: {}",this.getClass(), selectedEmails);
            for(String email : selectedEmails){
                giveAdminAccess(email);
                message = new Message("Admin Access has been provided", MessageType.green);
            }
        }
        else if ("reject".equals(action)) {
            logger.info("In {} Rejected emails: {}",this.getClass(), selectedEmails);
            logger.info("Going to delete from ADMIN_ACCESS_REQ table on rejection");
            for(String email : selectedEmails){
                AdminAccessReq req = adminAccessReqService.getByEmail(email);
                adminAccessReqService.deletePendingRequest(req);
                message = new Message("Request has been rejected.", MessageType.red);
            }
        }
        session.setAttribute("message", message);
        return "redirect:/admin/dashboard";
    }

    public void giveAdminAccess(String email) {
        User userToBeGivenAccess = userService.getUserByEmail(email);
        List<String> userExistingRoleList = userToBeGivenAccess.getRoleList();
        if (!roleService.hasAdminRole(userExistingRoleList)) {
            userExistingRoleList.add(AppConstants.ROLE_ADMIN);
            userToBeGivenAccess.setRoleList(userExistingRoleList);
            User u = userService.updateUser(userToBeGivenAccess).orElse(null);
            if (u != null) {
                //updating pending status(true) to false when access has been given
                AdminAccessReq existingReq = adminAccessReqService.getByUser(u);
                if(existingReq != null){
                    AdminAccessReq updatedReq = adminAccessReqService.updatePendingRequest(existingReq);
                }
                //sending mail for confirmation
                try {
                    String htmlContent = String.format(
                            AppConstants.htmlEmailTemplate,
                            userToBeGivenAccess.getName(),
                            "Admin Access has been granted.",
                            "",
                            "");
                    emailService.sendEmailWithHtml(
                            htmlContent,
                            u.getEmail(),
                            "Admin access request approved"
                    );
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @PostMapping("/search-user")
    public String SearchUser(@RequestParam("email") String email, HttpSession session, Model model){
        logger.info("In {}, Just entered SearchUser with email {}", this.getClass(), email);

        User user = userService.getUserByEmail(email);
        Message message = null;
        if(user != null){
            logger.info("In {}, User Found with email {}", this.getClass(), email);
            session.setAttribute("searchedUserData", user);
        }
        else{
            message = new Message("No User Found With the email", MessageType.red);

        }
        session.setAttribute("message", message);

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/revoke-role/{userId}")
    public String revokeAdminRole(@PathVariable String userId, HttpSession session){
        logger.info("In {}, Just entered revokeAdminRole method with user id {}", this.getClass(), userId);

        Message message = null;
        User user = userService.getUserById(userId);
        List<String> roleList = user.getRoleList();
        boolean isUserAdmin = roleService.hasAdminRole(roleList);
        if(user.getEmail().equals(AppConstants.APP_ADMIN_EMAIL)){
            message = new Message("Admin Role cannot be revoked", MessageType.red);
        }
        else {
            if(isUserAdmin){
                roleList.remove(AppConstants.ROLE_ADMIN);
                user.setRoleList(roleList);
                AdminAccessReq req = adminAccessReqService.getByEmail(user.getEmail());
                adminAccessReqService.deletePendingRequest(req);
                User updatedUser = userService.updateUser(user).orElse(null);
                if(updatedUser != null){
                    message = new Message("Admin Role has been revoked for this admin user", MessageType.green);
                }
            }
            else {
                message = new Message("The use is not an admin user", MessageType.red);
            }
        }
        session.setAttribute("message", message);
        return "redirect:/admin/dashboard";
    }
}
