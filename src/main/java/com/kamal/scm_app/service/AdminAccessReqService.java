package com.kamal.scm_app.service;

import com.kamal.scm_app.models.AdminAccessReq;
import com.kamal.scm_app.models.User;
import com.kamal.scm_app.repository.AdminAccessRepo;
import com.kamal.scm_app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminAccessReqService {

    @Autowired
    private AdminAccessRepo adminAccessRepo;

    @Autowired
    private UserService userService;

    public AdminAccessReq getByUser(User user){
        return adminAccessRepo.findByUser(user);
    }
    public AdminAccessReq getByEmail(String email) {
        return adminAccessRepo.findByEmail(email);
    }

    public List<AdminAccessReq> getAllRequests(){
        return adminAccessRepo.findAll();
    }

    public AdminAccessReq saveRequest(AdminAccessReq req){
        return adminAccessRepo.save(req);
    }

    public AdminAccessReq updatePendingRequest(AdminAccessReq adminAccessReq){

        if(adminAccessReq.isPending()){
            adminAccessReq.setPending(false);
        }
        else{
            adminAccessReq.setPending(true);
        }
        AdminAccessReq updatedReq = adminAccessRepo.save(adminAccessReq);
        return updatedReq;
    }

    public void deletePendingRequest(AdminAccessReq adminAccessReq){
        Long id = adminAccessRepo.findByEmail(adminAccessReq.getEmail()).getId();
        adminAccessRepo.deleteById(String.valueOf(id));
    }
}
