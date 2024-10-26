package com.kamal.scm_app.service.contact;

import com.kamal.scm_app.forms.AddContactForm;
import com.kamal.scm_app.service.ImageService;
import com.kamal.scm_app.service.user.UserService;
import com.kamal.scm_app.utils.exceptions.ResourceNotFoundException;
import com.kamal.scm_app.utils.Utility;
import com.kamal.scm_app.models.Contact;
import com.kamal.scm_app.models.SocialMediaLinks;
import com.kamal.scm_app.models.User;
import com.kamal.scm_app.repository.ContactRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ContactService {
    private final Logger logger = LoggerFactory.getLogger(ContactService.class);


    @Autowired
    private ContactRepo contactRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    public Contact getContactById(String contactId){
        return contactRepo.findById(contactId).orElse(new Contact());
    }

    public Contact saveContact(AddContactForm contactForm, Authentication authentication){

        String email = Utility.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        String contactId = UUID.randomUUID().toString();

        Contact contact = new Contact();
        contact.setId(contactId);
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        boolean isFav = contactForm.getIsFavourite().equalsIgnoreCase("yes");
        contact.setFavourite(isFav);
        contact.setUser(user);

        //saving social medial links
        SocialMediaLinks s1 = new SocialMediaLinks();
        SocialMediaLinks s2 = new SocialMediaLinks();

        s1.setSocialMediaLink(contactForm.getLinkedInLink());
        s1.setSocialMediaName("LinkedIn");
        s1.setContact(contact);
        s2.setSocialMediaLink(contactForm.getWebsiteLink());
        s2.setSocialMediaName("Personal Website");
        s2.setContact(contact);

        List<SocialMediaLinks> contactSocialList = new ArrayList<>();
        contactSocialList.add(s1);
        contactSocialList.add(s2);
        contact.setSocialMediaLinksList(contactSocialList);

        //process the contact image received and upload it
        String imageFileName = UUID.randomUUID().toString();
        logger.info("ContactService ==> Image received {}", contactForm.getContactPicture().getOriginalFilename());
        MultipartFile contactImage = contactForm.getContactPicture();
        String imageFileUrlFromCloud = imageService.uploadImageToCloud(contactImage, imageFileName);
        contact.setPicture(imageFileUrlFromCloud);
        contact.setCloudinaryImagePublicId(imageFileName);

        return contactRepo.save(contact);
    }

    public Contact updateContact(AddContactForm contact){
        var oldContact = contactRepo.findById(contact.getId()).orElseThrow(() -> new ResourceNotFoundException("Contact Does Not Exists."));

        oldContact.setName(contact.getName());
        oldContact.setEmail(contact.getEmail());
        oldContact.setAddress(contact.getAddress());
        oldContact.setDescription(contact.getDescription());
        oldContact.setPhoneNumber(contact.getPhoneNumber());
        boolean isFav = contact.getIsFavourite().equalsIgnoreCase("Yes");
        oldContact.setFavourite(isFav);

        //updating social medial links
        SocialMediaLinks s1 = new SocialMediaLinks();
        SocialMediaLinks s2 = new SocialMediaLinks();

        s1.setSocialMediaLink(contact.getLinkedInLink());
        s1.setContact(oldContact);
        s2.setSocialMediaLink(contact.getWebsiteLink());
        s2.setContact(oldContact);

        List<SocialMediaLinks> contactSocialList = new ArrayList<>();
        contactSocialList.add(s1);
        contactSocialList.add(s2);
//        oldContact.setSocialMediaLinksList(contactSocialList);

//        process the contact image received and upload it
        if(contact.getContactPicture() != null && !contact.getContactPicture().isEmpty()){
            String imageFileName = UUID.randomUUID().toString();
            logger.info("ContactService ==> Updating image {}", contact.getContactPicture().getOriginalFilename());
            MultipartFile contactImage = contact.getContactPicture();
            String imageFileUrlFromCloud = imageService.uploadImageToCloud(contactImage, imageFileName);
            oldContact.setPicture(imageFileUrlFromCloud);
            oldContact.setCloudinaryImagePublicId(imageFileName);
        }

        return contactRepo.save(oldContact);
    }

    public void deleteContactById(Contact contact){
        String id = contact.getId();
        contactRepo.deleteById(id);
    }

    public void deleteContact(Contact contact){
        contactRepo.delete(contact);
    }

    public List<Contact> getAllContacts(){
        List<Contact> allContacts = contactRepo.findAll();
        return allContacts;
    }

    public Page<Contact> searchContactsBy(
            String name, String email, String phone,
            int pageNo, int pageSize, String sortBy, String direction, String userId
    ){
        User user = userService.getUserById(userId);

        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(pageNo, pageSize, sort);

        if(name != null){
            logger.info("ContactService ==> Searching using name >>{}", name);
            return contactRepo.findByNameContainingIgnoreCaseAndUser(name,user, pageable);
        }
        if(email != null){
            logger.info("ContactService ==> Searching using email >> {}", email);
            return contactRepo.findByEmailContainingIgnoreCaseAndUser(email,user, pageable);
        }
        if(phone != null){
            logger.info("ContactService ==> Searching using phone >> {}", phone);
            return contactRepo.findByPhoneNumberContainingIgnoreCaseAndUser(phone,user, pageable);
        }
        return null;
    }

//    public List<Contact> getAllContactsByUser(String userId){
//        User user = userService.getUserById(userId);
//        List<Contact> contactList = contactRepo.findByUserId(user);
//        return contactList;
//    }

    public Page<Contact> getAllContactsByUser(String userId, int pageNo, int pageSize, String sortBy, String direction){
        User user = userService.getUserById(userId);

        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Contact> contactList = contactRepo.findByUser(user, pageable);
        return contactList;
    }
}
