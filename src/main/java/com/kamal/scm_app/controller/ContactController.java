package com.kamal.scm_app.controller;

import com.kamal.scm_app.forms.AddContactForm;
import com.kamal.scm_app.forms.ContactSearchForm;
import com.kamal.scm_app.models.SocialMediaLinks;
import com.kamal.scm_app.utils.AppConstants;
import com.kamal.scm_app.utils.alert_message.Message;
import com.kamal.scm_app.utils.alert_message.MessageType;
import com.kamal.scm_app.models.Contact;
import com.kamal.scm_app.service.contact.ContactService;
import com.kamal.scm_app.service.user.UserService;
import com.kamal.scm_app.utils.Utility;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;
    @Autowired
    private UserService userService;

    private final Logger logger = LoggerFactory.getLogger(ContactController.class);


    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addContactView(Model model){
        AddContactForm addContactForm = new AddContactForm();
        addContactForm.setWebsiteLink("https://kishorkamal.netlify.app/");
        model.addAttribute("contactForm", addContactForm);
        return "user/add_contact";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String saveContact(@Valid @ModelAttribute AddContactForm contactForm,
                              BindingResult result,
                              Authentication authentication,
                              HttpSession session){
        logger.info("{} ==> {}",this.getClass(), contactForm.toString());
        if(result.hasErrors()) {
            Message erorMessage = new Message("Please correct the following errors.", MessageType.red);
            session.setAttribute("message",erorMessage);
            return "redirect:/user/contacts/add";
        }
        Contact savedContact = contactService.saveContact(contactForm, authentication);
        if(savedContact != null){
            Message successMessage = new Message("You have successfully added the contact.", MessageType.green);
            session.setAttribute("message",successMessage);
        }
        return "redirect:/user/contacts/add";
    }

    @GetMapping
    public String viewContacts(
            @RequestParam(value = "pageNo",defaultValue = "0") int pageNo, @RequestParam(value = "pageSize",defaultValue = AppConstants.PAGE_SIZE) int pageSize,
            @RequestParam(value = "sortBy",defaultValue = "name") String sortBy, @RequestParam(value = "dir",defaultValue = "asc") String direction,
            Model model, Authentication authentication
    ){
        String email = Utility.getEmailOfLoggedInUser(authentication);
        String userId = userService.getUserByEmail(email).getUserId();
        Page<Contact> pageContacts = contactService.getAllContactsByUser(userId, pageNo, pageSize, sortBy, direction);

        model.addAttribute("pageContacts", pageContacts);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

        return "user/contacts";
    }

    @GetMapping("/search")
    public String filterContacts(
            @ModelAttribute ContactSearchForm contactSearchForm,
            @RequestParam(value = "pageNo",defaultValue = "0") int pageNo, @RequestParam(value = "pageSize",defaultValue = AppConstants.PAGE_SIZE) int pageSize,
            @RequestParam(value = "sortBy",defaultValue = "name") String sortBy, @RequestParam(value = "dir",defaultValue = "asc") String direction,
            Model model, Authentication authentication){
        String userEmail = Utility.getEmailOfLoggedInUser(authentication);
        String userId = userService.getUserByEmail(userEmail).getUserId();

        String field = contactSearchForm.getField();
        String keyword = contactSearchForm.getKeyword();

        logger.info("ContactController ==>For user {}, received field:{}, keyword:{}", userEmail,field, keyword);
        Page<Contact> pageContacts = null;
        String name = null, email = null, phone = null;
        if(field.equalsIgnoreCase("name")){
            name = keyword.toLowerCase();
        } else if (field.equalsIgnoreCase("email")) {
            email = keyword.toLowerCase();
        } else if (field.equalsIgnoreCase("phoneNumber")) {
            phone = keyword.toLowerCase();
        }
        pageContacts = contactService.searchContactsBy(
                name,email,phone,
                pageNo, pageSize, sortBy, direction, userId);
        model.addAttribute("pageContacts", pageContacts);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);
        model.addAttribute("contactSearchForm", contactSearchForm);

        return "user/search";
    }
    @GetMapping("/delete/{contactId}")
    public String deleteContact(@PathVariable String contactId, HttpSession session){

        Contact contact = contactService.getContactById(contactId);
        if(contact != null){
            contactService.deleteContactById(contact);
            Message message = new Message("Contact has been delete successfully !!", MessageType.red);
            session.setAttribute("message", message);
            logger.info("ContactController ==> Contact deleted {}", contactId);
        }
        else{
            logger.info("ContactController ==> No Contact found for contact id {}", contactId);
        }
        return "redirect:/user/contacts";
    }

    @GetMapping("/view/{contactId}")
    public String viewUpdateContactForm(@PathVariable String contactId, Model model){
        Contact contact = contactService.getContactById(contactId);
        AddContactForm contactForm = new AddContactForm();
        if(contact != null){
            contactForm.setId(contact.getId());
            contactForm.setName(contact.getName());
            contactForm.setEmail(contact.getEmail());
            contactForm.setAddress(contact.getAddress());
            contactForm.setDescription(contact.getDescription());
            contactForm.setPhoneNumber(contact.getPhoneNumber());
            contactForm.setIsFavourite(contact.isFavourite() ? "Yes": "No");
            contactForm.setContactPictureUrl(contact.getPicture());
            List<SocialMediaLinks> scLinks = contact.getSocialMediaLinksList();
            scLinks.forEach(s -> {
                if(s.getSocialMediaName().equals("LinkedIn")){
                    contactForm.setLinkedInLink(s.getSocialMediaLink());
                } else if (s.getSocialMediaName().equals("Personal Website")) {
                    contactForm.setWebsiteLink(s.getSocialMediaLink());
                }
            });
        }
        model.addAttribute("contactForm", contactForm);
        return "user/update_contact_view";
    }

    @PostMapping("/update/{contactId}")
    public String updateContact(@PathVariable String contactId,@Valid @ModelAttribute AddContactForm contactForm,
                                Model model, HttpSession session, BindingResult rBindingResult){
        if(rBindingResult.hasErrors()){
            return "user/update_contact_view";
        }

        logger.info("ContactController ==> Updating the Contact with id {}", contactId);
        Contact contact = contactService.getContactById(contactId);
        if(contact != null){
            contactForm.setId(contact.getId());
            contact = contactService.updateContact(contactForm);
            Message message = new Message("Contact updated Successfully.", MessageType.green);
            session.setAttribute("message", message);
        }
        else{
            Message message = new Message("Contact Does Not Exists.", MessageType.red);
            session.setAttribute("message", message);
        }
        return "redirect:/user/contacts/view/" + (contact != null ? contact.getId() : null);
    }
}
