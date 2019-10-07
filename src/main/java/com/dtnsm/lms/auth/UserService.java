package com.dtnsm.lms.auth;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    Account findByUserId(String userId);

    Account save(UserRegistrationDto registration);
}