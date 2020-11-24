package com.dtnsm.lms.auth;

import com.dtnsm.lms.domain.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    Account findByUserId(String userId);

    Account save(Account registration);

    Optional<List<Account>> findByParentUserId(String userId);

    Optional<List<Account>> findAllByParentUserIdAndEnabled(String userId, Boolean enabled);
}