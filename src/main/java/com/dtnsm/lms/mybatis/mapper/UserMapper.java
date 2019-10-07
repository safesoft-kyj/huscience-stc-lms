package com.dtnsm.lms.mybatis.mapper;

import com.dtnsm.lms.mybatis.dto.UserVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    UserVO selectUserById(String userId);
    List<UserVO> selectAll();
    List<UserVO> selectAllByOrgCode(String orgCode);
}
