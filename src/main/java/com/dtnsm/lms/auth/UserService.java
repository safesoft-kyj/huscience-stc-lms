package com.dtnsm.lms.auth;

import com.dtnsm.lms.domain.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    Account findByUserId(String userId);

    Account save(Account registration);
}