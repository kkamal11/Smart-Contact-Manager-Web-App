package com.kamal.scm_app.repository;

import com.kamal.scm_app.models.AdminAccessReq;
import com.kamal.scm_app.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminAccessRepo extends JpaRepository<AdminAccessReq, String> {

    AdminAccessReq findByUser(User user);
    AdminAccessReq findByEmail(String email);
}
