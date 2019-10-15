package com.dtnsm.lms.controller.RestController;

import com.dtnsm.lms.mybatis.dto.DepartTreeVO;
import com.dtnsm.lms.mybatis.service.DepartMapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/depart")
public class DepartRestController {

    @Autowired
    DepartMapperService departMapperService;

    @GetMapping("/departViewTree")
    public List<DepartTreeVO> treeViewGet(){
        List<DepartTreeVO> treeListData = departMapperService.getDepartTree();
        return treeListData;
    }

    @PostMapping("/departViewTree")
    public List<DepartTreeVO> treeViewPost(){
        List<DepartTreeVO> treeListData = departMapperService.getDepartTree();
        return treeListData;
    }

}
