package com.dtnsm.lms.mybatis.mapper;

import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.mybatis.dto.CVFindParam;
import com.dtnsm.lms.mybatis.dto.CVFindResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CVFinderMapper {
    List<CVFindResult> findCV(CVFindParam param);

    List<Account> findUpdateBinderUsers();

    List<Account> findBinderRegistUsers();

    List<String> findByParentUserId(String parentUserId);
}
