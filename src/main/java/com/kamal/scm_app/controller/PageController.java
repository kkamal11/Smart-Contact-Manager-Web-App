package com.kamal.scm_app.controller;

import com.kamal.scm_app.forms.UserRegisterForm;
import com.kamal.scm_app.utils.alert_message.Message;
import com.kamal.scm_app.utils.alert_message.MessageType;
import com.kamal.scm_app.models.User;
import com.kamal.scm_app.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class PageController {

    @Value("${defaultProfilePicLink}") // Injecting the value of defaultProfilePicLink from application.properties
    private String defaultProfilePicLink;

    @Autowired
    UserService userService;

    @RequestMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public String home() {
        return "home";
    }

    @RequestMapping("/about")
    public String aboutPage() {
        return "about";
    }

    @RequestMapping("/services")
    public String servicesPage() {
        return "service";
    }

    @RequestMapping("/contact")
    public String contactPage() {
        return "contact";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPageGET() {
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginPage() {
        return "login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerPage(Model model) {
        UserRegisterForm userRegisterForm = new UserRegisterForm();
        userRegisterForm.setAbout("I am ...");
        model.addAttribute("userRegisterForm", userRegisterForm);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute UserRegisterForm userRegisterForm, BindingResult rBindingResult, HttpSession session) {

        //Validate form data
        if(rBindingResult.hasErrors()){
            return "register";
        }

        User user = new User();
        user.setEmail(userRegisterForm.getEmail());
        user.setName(userRegisterForm.getUserName());
        user.setPassword(userRegisterForm.getPassword());
        user.setAbout(userRegisterForm.getAbout());
        user.setPhoneNumber(userRegisterForm.getPhoneNumber());
        user.setProfilePicLink(defaultProfilePicLink);
        user.setGender(userRegisterForm.getGender());
        User savedUser = userService.saveUser(user);
        if(savedUser != null){
            Message message = new Message("Registration Successful and verification email sent. Please verify first to login.", MessageType.green);
            session.setAttribute("message",message);
        }
        return "redirect:/register";
    }


    @RequestMapping("/admin/create-admin")
    public String createAdminUser(){
        return "";
    }
}
