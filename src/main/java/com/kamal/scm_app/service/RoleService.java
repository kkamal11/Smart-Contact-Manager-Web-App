package com.kamal.scm_app.service;

import com.kamal.scm_app.models.User;
import com.kamal.scm_app.service.user.UserService;
import com.kamal.scm_app.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private UserService userService;

    public List<String> getRolesOfLoggedInUser(String email){
        User user = userService.getUserByEmail(email);
        return user.getRoleList();
    }

    public boolean hasAdminRole(List<String> rolesList){
        return rolesList.contains(AppConstants.ROLE_ADMIN);
    }

    public boolean hasUserRole(List<String> rolesList){
        return rolesList.contains(AppConstants.ROLE_USER);
    }

    public boolean hasEditorRole(List<String> rolesList){
        return rolesList.contains(AppConstants.ROLE_EDITOR);
    }
}


