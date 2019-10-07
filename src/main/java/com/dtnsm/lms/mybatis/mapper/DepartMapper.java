package com.dtnsm.lms.mybatis.mapper;

import com.dtnsm.lms.mybatis.dto.DepartTreeVO;
import com.dtnsm.lms.mybatis.dto.DepartVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DepartMapper {

    DepartVO selectDepartByOrgCode(String orgCode);
    List<DepartVO> selectAll();
    List<DepartTreeVO> selectTree();

}
