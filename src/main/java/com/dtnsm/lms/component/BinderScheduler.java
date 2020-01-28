package com.dtnsm.lms.component;

import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.constant.BinderAlarmType;
import com.dtnsm.lms.mybatis.mapper.CVFinderMapper;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.service.Mail;
import com.dtnsm.lms.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.context.Context;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinderScheduler {
    private final CVFinderMapper cvFinderMapper;
    private final UserRepository userRepository;
    private final MailService mailService;
    private int[] days = {-21, -14, -7, -3, -1};
    private String[] strDays = {"3주", "2주", "1주", "3일", "1일"};
    // 매일 오전 0시 10분에 실행
    @Scheduled(cron = "0 10 0 * * *")
    public void binderCheck() {

        for(int i = 0; i < days.length; i ++) {
            binderUpdateAlert(i);
        }
    }


    /**
     * 3주,2주,1주,3일,1일
     */
    private void binderUpdateAlert(int index) {

        List<String> users = cvFinderMapper.findUpdateBinderUsers(days[index]);
        if(!ObjectUtils.isEmpty(users)) {
            log.info("Binder Update[day:{}] 알림 대상자가 존재함. 대상자 수 : {}", strDays[index], users.size());
            Mail mail = new Mail();
            for(String username : users) {
                Account account = userRepository.findByUserId(username);
                mail.setEmail(account.getEmail());

                Context context = new Context();
                context.setVariable("empName", account.getName());
                context.setVariable("remainDay", strDays[index]);
                mailService.send(account.getEmail(), String.format(BinderAlarmType.BINDER_UPDATE.getTitle(), account.getName()), BinderAlarmType.BINDER_UPDATE, context);
            }
        } else {
            log.info("Binder Update[day:{}] 알림 대상자가 없습니다.", strDays[index]);
        }
    }
}
