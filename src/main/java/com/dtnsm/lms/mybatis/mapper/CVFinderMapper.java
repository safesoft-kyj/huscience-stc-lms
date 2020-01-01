package com.dtnsm.lms.mybatis.mapper;

import com.dtnsm.lms.mybatis.dto.CVFindParam;
import com.dtnsm.lms.mybatis.dto.CVFindResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CVFinderMapper {
    List<CVFindResult> findCV(CVFindParam param);
}
