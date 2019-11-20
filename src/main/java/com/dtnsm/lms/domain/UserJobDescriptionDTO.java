package com.dtnsm.lms.domain;

import com.dtnsm.common.entity.UserJobDescription;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class UserJobDescriptionDTO {
    private Account account;
    private UserJobDescription userJobDescription;
    @QueryProjection
    public UserJobDescriptionDTO(Account account, UserJobDescription userJobDescription) {
        this.account = account;
        this.userJobDescription = userJobDescription;
    }
}
