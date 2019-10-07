package com.dtnsm.lms.audit;

import com.dtnsm.lms.auth.CustomUserDetails;
import com.dtnsm.lms.auth.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class CommonAuditorAware implements AuditorAware<String> {

    @Autowired
    UserServiceImpl userService;

    @Override
    public Optional<String> getCurrentAuditor() {

        //UserDetails customUserDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()){
            return Optional.empty();
        }

        String userId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        //String userId = ((UserDetails) SecurityContextHolder.getContext().getAuthentication()).getUsername();

        return Optional.of(userId);
    }
}


