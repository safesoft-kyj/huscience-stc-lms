package com.dtnsm.lms.Controller;

import com.dtnsm.lms.util.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
public class MyPageController {

    private PageInfo pageInfo = new PageInfo();

    public MyPageController() {
        pageInfo.setParentId("m-mypage");
        pageInfo.setParentTitle("마이페이지");
    }

    @GetMapping("/main")
    public String main(Model model) {

        pageInfo.setPageId("m-mypage-main");
        pageInfo.setPageTitle("메인");
        model.addAttribute(pageInfo);

        return "content/mypage/main";
    }

    @GetMapping("/quiz")
    public String quiz(Model model) {

        pageInfo.setPageId("m-mypage-quiz");
        pageInfo.setPageTitle("시험/시험결과/수료증발급");
        model.addAttribute(pageInfo);

        return "content/mypage/main";
    }

    @GetMapping("/approval")
    public String approval(Model model) {

        pageInfo.setPageId("m-mypage-approval");
        pageInfo.setPageTitle("교육결재조회");
        model.addAttribute(pageInfo);

        return "content/mypage/approval";
    }

    @GetMapping("/myinfo")
    public String myInfo(Model model) {

        pageInfo.setPageId("m-mypage-myinfo");
        pageInfo.setPageTitle("사용자정보");
        model.addAttribute(pageInfo);

        return "content/mypage/myinfo";
    }
}
