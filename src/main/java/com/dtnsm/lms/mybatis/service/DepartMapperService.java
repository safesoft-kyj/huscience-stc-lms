package com.dtnsm.lms.mybatis.service;

import com.dtnsm.lms.mybatis.dto.DepartTreeVO;
import com.dtnsm.lms.mybatis.dto.DepartVO;
import com.dtnsm.lms.mybatis.mapper.DepartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class DepartMapperService {

    @Autowired
    private DepartMapper departMapper;

    public List<DepartVO> getDepartAll() {
        return departMapper.selectAll();
    }
    public List<DepartTreeVO> getDepartTree() {
        return departMapper.selectTree();
    }

    public DepartVO getDepartByOrgCode(String orgCode) {
        return departMapper.selectDepartByOrgCode(orgCode);
    }

}
