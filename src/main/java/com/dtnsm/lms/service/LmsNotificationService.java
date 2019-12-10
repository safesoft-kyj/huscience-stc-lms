package com.dtnsm.lms.service;

import com.dtnsm.lms.auth.UserService;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.LmsNotification;
import com.dtnsm.lms.domain.Role;
import com.dtnsm.lms.domain.constant.LmsAlarmGubun;
import com.dtnsm.lms.domain.constant.LmsAlarmType;
import com.dtnsm.lms.repository.LmsNotificationRepository;
import com.dtnsm.lms.repository.RoleRepository;
import com.dtnsm.lms.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LmsNotificationService {

    @Autowired
    LmsNotificationRepository lmsNotificationRepository;

    @Autowired
    UserService userService;

    public List<LmsNotification> getAllByUserNotification(String userId) {
        return lmsNotificationRepository.findAllByAccount_UserId(userId);
    }

    public List<LmsNotification> getTop5ByUserNotification(String userId) {
        return lmsNotificationRepository.findTop5ByAccount_UserIdOrderByCreatedDateDesc(userId);
    }

    public LmsNotification save(LmsNotification lmsNotification) {

        return lmsNotificationRepository.save(lmsNotification);
    }

    public void delete(LmsNotification lmsNotification) {
        lmsNotificationRepository.delete(lmsNotification);
    }


    public void createAlarm(LmsAlarmType lmsAlarmType, Account account) {

        LmsNotification lmsNotification = new LmsNotification();

        if (lmsAlarmType == LmsAlarmType.ParentUser) {
            lmsNotification.setAlarmGubun(LmsAlarmGubun.WARNING);
            lmsNotification.setTitle("<a class='text-info' href='/mypage/myInfo'>상위결재권자 미설정</a>");
            lmsNotification.setContent("상위결재권자는 교육신청이나 전자결재 전 필히 지정하셔야 합니다.");

        } else if (lmsAlarmType == LmsAlarmType.Sign) {
            lmsNotification.setAlarmGubun(LmsAlarmGubun.INFO);
            lmsNotification.setTitle("<a class='text-info' href='/mypage/myInfo'>사인 미등록<a>");
            lmsNotification.setContent("사인이 미등록 되었습니다.");
        } else if (lmsAlarmType == LmsAlarmType.Manager) {
            lmsNotification.setAlarmGubun(LmsAlarmGubun.WARNING);
            lmsNotification.setTitle("과정 관리자 미등록");
            lmsNotification.setContent("과정 관리자는 필히 지정하셔야 합니다.");
        }

        lmsNotification.setAccount(account);
        lmsNotificationRepository.save(lmsNotification);
    }
}
