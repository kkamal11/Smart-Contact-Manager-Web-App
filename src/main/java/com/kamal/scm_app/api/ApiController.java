package com.kamal.scm_app.api;

import com.kamal.scm_app.models.Contact;
import com.kamal.scm_app.models.User;
import com.kamal.scm_app.service.contact.ContactService;
import com.kamal.scm_app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    @GetMapping("/contacts/{contactId}")
    public Contact getContactById(@PathVariable String contactId){
        return contactService.getContactById(contactId);
    }

    @GetMapping("/user-data/{email}")
    public User getUserDetails(@PathVariable("email") String email){
        return userService.getUserByEmail(email);
    }

}
