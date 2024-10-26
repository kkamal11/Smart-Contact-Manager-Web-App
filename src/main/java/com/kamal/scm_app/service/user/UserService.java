package com.kamal.scm_app.service.user;

import com.kamal.scm_app.service.email.EmailService;
import com.kamal.scm_app.utils.AppConstants;
import com.kamal.scm_app.utils.exceptions.ResourceNotFoundException;
import com.kamal.scm_app.models.User;
import com.kamal.scm_app.repository.UserRepo;
import com.kamal.scm_app.utils.Utility;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public User getUserById(String userId){
        return userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
    }

    public User getUserByEmail(String email){
        return userRepo.findByEmail(email).orElse(null);
    }

    public User getUserByToken(String token){
        return userRepo.findByEmailVerificationToken(token).orElse(null);
    }

    public User saveUser(User user){
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);

        //encode the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        //set the role for user > hardcoded currently
        user.setRoleList(List.of(AppConstants.ROLE_USER));

        logger.info("UserService ==> User Provider : {}",user.getProvider().toString());

        LocalDate currentDate = LocalDate.now();
        user.setJoinedOn(currentDate);

        //generate the token and send mail to the user for verification
        long currentTimeInMilliSecond = Instant.now().toEpochMilli();
        String emailToken = UUID.randomUUID().toString() + "~" + currentTimeInMilliSecond + "~" + UUID.randomUUID().toString();

        user.setEmailVerificationToken(emailToken);
        User savedUser = userRepo.save(user);

        String emailVerificationLink = Utility.getLinkForEmailVerification(emailToken);
//        emailService.sendEmail(savedUser.getEmail(),"Verify account",emailVerificationLink);

        try {
            emailService.sendRegisterEmailWithHtml(
                    savedUser.getName(),
                    savedUser.getEmail(),
                    "Verify Your Account",
                    emailVerificationLink);
        }
        catch (MessagingException mEx){
            System.out.println(mEx.getMessage());
        }
        return savedUser;
    }
    public Optional<User> updateUser(User user){
        User savedUser = null;
        User userToBeUpdated = userRepo.findById(user.getUserId()).orElseThrow(() ->{
            throw new ResourceNotFoundException("User Not Found");
        });
        if(userToBeUpdated != null){
            userToBeUpdated.setName(user.getName());
            userToBeUpdated.setEmail(user.getEmail());
            userToBeUpdated.setPassword(user.getPassword());
            userToBeUpdated.setPhoneNumber(user.getPhoneNumber());
            userToBeUpdated.setAbout(user.getAbout());
            userToBeUpdated.setProfilePicLink(user.getProfilePicLink());
            userToBeUpdated.setEnabled(user.isEnabled());
            userToBeUpdated.setEmailVerified(user.isEmailVerified());
            userToBeUpdated.setPhoneVerified(user.isPhoneVerified());
            userToBeUpdated.setContactList(user.getContactList());
            userToBeUpdated.setProviderUserId(user.getProviderUserId());
            logger.info("UserService ==> User updated with email : {}",user.getEmail());
            savedUser = userRepo.save(userToBeUpdated);
        }
        return Optional.ofNullable(savedUser);
    }
    public void deleteUser(User user){
        User userToBeDeleted = userRepo.findById(user.getUserId()).orElseThrow(() ->{
            throw new ResourceNotFoundException("User Not Found");
        });
        userRepo.delete(userToBeDeleted);
    }

    public boolean doesUserExistById(String userId){
        User userExists = userRepo.findById(userId).orElse(null);
        return userExists != null;

    }
    public boolean doesUserExistByEmail(String email){
        User userExists = (User) userRepo.findByEmail(email).orElse(null);
        return userExists != null;
    }

    public List<User> getAllUsers(){
        return userRepo.findAll();
    }

    public List<User> getAllActiveUsers(){
        List<User> allActiveUsers = userRepo.findAll();
        return allActiveUsers;
    }
}
