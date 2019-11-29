package com.dtnsm.lms.domain;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class UserCurriculumVitaeDTO {
    private Account account;
    private CurriculumVitae curriculumVitae;
    @QueryProjection
    public UserCurriculumVitaeDTO(Account account, CurriculumVitae curriculumVitae) {
        this.account = account;
        this.curriculumVitae = curriculumVitae;
    }
}
