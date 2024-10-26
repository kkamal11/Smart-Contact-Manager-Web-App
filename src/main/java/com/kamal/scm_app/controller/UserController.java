package com.kamal.scm_app.controller;

import com.kamal.scm_app.models.Contact;
import com.kamal.scm_app.models.User;
import com.kamal.scm_app.service.AdminAccessReqService;
import com.kamal.scm_app.service.RoleService;
import com.kamal.scm_app.service.contact.ContactService;
import com.kamal.scm_app.service.user.UserService;
import com.kamal.scm_app.utils.AdminDashboardData;
import com.kamal.scm_app.utils.Utility;
import com.kamal.scm_app.utils.alert_message.Message;
import com.kamal.scm_app.utils.alert_message.MessageType;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Controller
@RequestMapping("/user")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private ContactService contactService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AdminAccessReqService adminAccessReqService;

    @GetMapping("/dashboard")
    public String userDashboard(Authentication authentication, Model model){
        String loggedInUserEmail = Utility.getEmailOfLoggedInUser(authentication);
        List<String> userRoles = roleService.getRolesOfLoggedInUser(loggedInUserEmail);
        boolean isUserAdmin = roleService.hasAdminRole(userRoles);
        if(isUserAdmin){

            List<User> users = userService.getAllUsers();
            List<Contact> contacts = contactService.getAllContacts();
            List<String> reqPendingEmails = adminAccessReqService.getAllRequests()
                    .stream().map((r) -> {
                        if(r.isPending()){
                            return r.getEmail();
                        }
                        return null;
                    }).filter(Objects::nonNull).toList();

            AdminDashboardData data = new AdminDashboardData();
            data.setTotalUserCount(users.size());
            data.setVerifiedUserCount((int) users.stream().map(User::isEnabled).count());
            data.setTotalContactsCount(contacts.size());
            data.setTotalAdminCount((int) users.stream().filter(u -> roleService.hasAdminRole(u.getRoleList())).count());
            data.setPendingEmailsForAdminAccess(reqPendingEmails);

            model.addAttribute("adminData", data);
            model.addAttribute("selectedEmails", new ArrayList<>());
            return "user/dashboard";
        }
        return "user/user_dashboard";
    }

    @GetMapping("/my-profile")
    public String userProfile(Model model){
        return "user/profile";
    }

    @GetMapping("/delete-user/{userId}")
    public String deleteUser(@PathVariable String userId, HttpSession session){
        logger.info("In {}, Just entered deleteUser method with user id {}", this.getClass(), userId);

        Message message;

        User user = userService.getUserById(userId);
        boolean isUserAdmin = roleService.hasAdminRole(user.getRoleList());
        if(!isUserAdmin){
            userService.deleteUser(user);
            message = new Message("User has been deleted", MessageType.green);
        }
        else {
            message = new Message("The given user is an Admin. Cannot delete.", MessageType.red);
        }
        session.setAttribute("message", message);
        return "redirect:/user/dashboard";
    }

}
