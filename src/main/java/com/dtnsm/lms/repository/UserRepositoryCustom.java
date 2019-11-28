package com.dtnsm.lms.repository;

import com.dtnsm.common.entity.constant.JobDescriptionStatus;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.UserCurriculumVitaeDTO;
import com.dtnsm.lms.domain.UserJobDescriptionDTO;
import com.dtnsm.lms.domain.constant.CurriculumVitaeStatus;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<List<Account>> findByParentUserId(String userId);

    List<UserJobDescriptionDTO> getUserJobDescriptionByParentUserId(String parentUserId, JobDescriptionStatus status);

    List<UserCurriculumVitaeDTO> getUserCurriculumVitaeList(String parentUserId, CurriculumVitaeStatus status);
}
