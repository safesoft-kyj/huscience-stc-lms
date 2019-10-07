package com.dtnsm.lms.Controller;

import com.dtnsm.lms.util.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/info")
public class InfoController {

    private PageInfo pageInfo = new PageInfo();

    public InfoController() {
        pageInfo.setParentId("m-info");
        pageInfo.setParentTitle("교육안내");
    }

    @GetMapping("/month")
    public String month(Model model) {

        pageInfo.setPageId("m-info-month");
        pageInfo.setPageTitle("교육월간일정");
        model.addAttribute(pageInfo);

        return "content/info/month";
    }

    @GetMapping("/year")
    public String year(Model model) {

        pageInfo.setPageId("m-info-year");
        pageInfo.setPageTitle("교육연간일정");
        model.addAttribute(pageInfo);

        return "content/info/year";
    }

    @GetMapping("/request")
    public String request(Model model) {

        pageInfo.setPageId("m-info-request");
        pageInfo.setPageTitle("교육신청");
        model.addAttribute(pageInfo);

        return "content/info/request";
    }
}
