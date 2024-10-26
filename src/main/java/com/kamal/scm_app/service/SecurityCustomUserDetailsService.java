package com.kamal.scm_app.service;

import com.kamal.scm_app.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SecurityCustomUserDetailsService implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(SecurityCustomUserDetailsService.class);


    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User Not Found."));

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

//        return new User(user.getEmail(), user.getPassword(), authorities);
        return user;
    }
}
