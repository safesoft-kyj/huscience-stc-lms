package com.dtnsm.lms.controller;

import com.dtnsm.lms.domain.LmsNotification;
import com.dtnsm.lms.domain.QLmsNotification;
import com.dtnsm.lms.repository.LmsNotificationRepository;
import com.dtnsm.lms.util.PageInfo;
import com.dtnsm.lms.util.SessionUtil;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/notification")
public class LmsNotificationController {

    @Autowired
    LmsNotificationRepository lmsNotificationRepository;

    private PageInfo pageInfo = new PageInfo();

    public LmsNotificationController() {
        pageInfo.setParentId("m-customer");
        pageInfo.setParentTitle("알람 로그");
    }

    @GetMapping("")
    public String listPage(@PageableDefault Pageable pageable, Model model) {

        String userId = SessionUtil.getUserId();

        QLmsNotification qLmsNotification = QLmsNotification.lmsNotification;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qLmsNotification.account.userId.eq(userId));

        Page<LmsNotification> borders;
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
//        List<Sort.Order> order = new ArrayList<Sort.Order>();
//        order.add(new Sort.Order(Sort.Direction.ASC, "name"));
//        order.add(new Sort.Order(Sort.Direction.DESC, "email"));
//        pageable = PageRequest.of(page, 10, Sort.by(order));
        pageable = PageRequest.of(page, 10, Sort.by("id").descending());

        borders = lmsNotificationRepository.findAll(builder, pageable);

        pageInfo.setPageId("m-customer-list-page");
        pageInfo.setPageTitle("알람 로그");
        model.addAttribute(pageInfo);
        model.addAttribute("borders", borders);

        return "content/notification/list";
    }

    @GetMapping("/view/{id}")
    public String viewPage(@PathVariable("id") long id, Model model) {

        pageInfo.setPageTitle("알람 로그");

        QLmsNotification qLmsNotification = QLmsNotification.lmsNotification;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qLmsNotification.id.eq(id));
        Optional<LmsNotification> lmsNotification = lmsNotificationRepository.findOne(builder);

        LmsNotification lmsNotification1;
        if (lmsNotification.isPresent()) {
            lmsNotification1 = lmsNotification.get();
        } else {
            lmsNotification1 = new LmsNotification();
        }

        model.addAttribute(pageInfo);
        model.addAttribute("notification", lmsNotification1);

        return "content/notification/view";
    }
}
