package com.dtnsm.lms.mybatis.service;

import com.dtnsm.lms.mybatis.dto.UserVO;
import com.dtnsm.lms.mybatis.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserMapperService {

    @Autowired
    private UserMapper userMapper;

    public List<UserVO> getUserAll() {
        return userMapper.selectAll();
    }

    public UserVO getUserById(String userId) {
        return userMapper.selectUserById(userId);
    }

    public List<UserVO> getUserAllByOrgCode(String orgCode) {
        return userMapper.selectAllByOrgCode(orgCode);
    }

}
