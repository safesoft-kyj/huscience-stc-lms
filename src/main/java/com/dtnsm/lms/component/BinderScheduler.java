package com.dtnsm.lms.component;

import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.constant.BinderAlarmType;
import com.dtnsm.lms.mybatis.mapper.CVFinderMapper;
import com.dtnsm.lms.repository.UserRepository;
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
    // 매일 오전 0시 10분에 실행
    @Scheduled(cron = "0 10 0 * * *")
    public void binderCheck() {

//        for(int i = 0; i < days.length; i ++) {
        binderUpdateAlert();
//        }

        binderRegistUsers();
    }


    private String toDayString(int day) {
        if(day == 21) {
            return "3주";
        } else if(day == 14) {
            return "2주";
        } else if(day == 7) {
            return "1주";
        } else if(day == 3) {
            return "3일";
        } else if(day == 1) {
            return "1일";
        }

        return "";
    }
    /**
     * 3주,2주,1주,3일,1일
     */
    private void binderUpdateAlert() {

        List<Account> users = cvFinderMapper.findUpdateBinderUsers();
        if(!ObjectUtils.isEmpty(users)) {
//            log.info("Binder Update 알림 대상자가 존재함. 대상자 수 : {}", users.size());
//            Mail mail = new Mail();
            for(Account account : users) {
//                mail.setEmail(account.getEmail());

                Context context = new Context();
                context.setVariable("empName", account.getName());
                context.setVariable("remainDay", toDayString(account.getRemainDay()));
                mailService.send(account.getEmail(), String.format(BinderAlarmType.BINDER_UPDATE.getTitle(), account.getName()), BinderAlarmType.BINDER_UPDATE, context);
            }
        } else {
//            log.info("Binder Update 알림 대상자가 없습니다.");
        }
    }

    /**
     * 7, 3, 1일전 사용자에게 바인더 등록 권고 메일 전송
     */
    public void binderRegistUsers() {
        List<Account> users = cvFinderMapper.findBinderRegistUsers();
        if(!ObjectUtils.isEmpty(users)) {
            for(Account user : users) {
                Context context = new Context();
                context.setVariable("empName", user.getName());
                context.setVariable("remainDay", user.getRemainDay());

                mailService.send(user.getEmail(), String.format(BinderAlarmType.BINDER_REG.getTitle(), user.getName()), BinderAlarmType.BINDER_REG, context);
            }
        }
    }
}
