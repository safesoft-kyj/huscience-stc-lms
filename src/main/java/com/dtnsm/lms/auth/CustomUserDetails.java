package com.dtnsm.lms.auth;


import com.dtnsm.common.entity.QUserJobDescription;
import com.dtnsm.common.entity.UserJobDescription;
import com.dtnsm.common.entity.constant.JobDescriptionStatus;
import com.dtnsm.common.repository.UserJobDescriptionRepository;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.util.GlobalUtil;
import com.querydsl.core.BooleanBuilder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter
@Setter
public class CustomUserDetails implements UserDetails {

    Account user;

    private boolean manager;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    public String getUserId() {
        return user.getUserId();
    }

    public String getJobTitle() {

//        Iterable<UserJobDescription> userJobDescriptions = getJobDescriptionList(user.getUserId(), Arrays.asList(JobDescriptionStatus.APPROVED));
//

        return GlobalUtil.getJobDescriptionString(user.getUserId());
//        return "jobTitle";
    }

    public String getOrgDept() {
        return user.getOrgDepart();
    }

    public String getEngName() {
        return user.getEngName();
    }

    @Override
        public String getUsername() {
        return user.getName();
    }

    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }



}
