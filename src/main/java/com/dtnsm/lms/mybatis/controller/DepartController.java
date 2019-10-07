package com.dtnsm.lms.mybatis.controller;

import com.dtnsm.lms.mybatis.dto.DepartVO;
import com.dtnsm.lms.mybatis.service.DepartMapperService;
import com.dtnsm.lms.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/depart")
public class DepartController {

    @Autowired
    DepartMapperService departMapperService;

    PageInfo pageInfo = new PageInfo();

    @GetMapping("/list")
    public String list(Model model) {

        pageInfo.setPageTitle("부서조회");

        List<DepartVO> vo = departMapperService.getDepartAll();

        model.addAttribute(pageInfo);
        model.addAttribute("borders", vo);

        return "admin/depart/list";
    }

    @GetMapping("/treeView")
    public String treeView(Model model) {

        pageInfo.setPageTitle("트리");

        model.addAttribute(pageInfo);

        return "admin/depart/treeView";
    }

}
