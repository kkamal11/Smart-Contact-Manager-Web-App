package com.kamal.scm_app.repository;

import com.kamal.scm_app.models.Contact;
import com.kamal.scm_app.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepo extends JpaRepository<Contact, String> {

//    List<Contact> findByUserId(User user);

    //    List<Contact> findByUser(User user); //this method we can get for free as we have the user field present
    Page<Contact> findByUser(User user, Pageable pageable);
    Page<Contact> findByNameContainingIgnoreCaseAndUser(String keyword, User user, Pageable pageable);
    Page<Contact> findByEmailContainingIgnoreCaseAndUser(String keyword, User user, Pageable pageable);
    Page<Contact> findByPhoneNumberContainingIgnoreCaseAndUser(String keyword, User user, Pageable pageable);

    @Query(
            "SELECT c FROM Contact c WHERE c.user = :userId"
    )
    List<Contact> findContactByUserId(@Param("userId") String userId);
}
