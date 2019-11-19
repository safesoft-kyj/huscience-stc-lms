package com.dtnsm.lms.repository;

import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.UserJobDescriptionDTO;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<List<Account>> findByParentUserId(String userId);

    List<UserJobDescriptionDTO> getUserJobDescriptionByParentUserId(String userId);
}
