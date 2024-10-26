package com.kamal.scm_app.utils;


import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class CustomSession {
    public static void removeMessage(){
    try {
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
        session.removeAttribute("message");
        session.removeAttribute("searchedUserData");
    } catch (Exception e) {
        e.printStackTrace();
    }
    }
}
