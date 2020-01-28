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
    // 매일 오전 0시 10분에 실행
    @Scheduled(cron = "0 10 0 * * *")
    public void binderCheck() {
        //11개월 330일이 되는 시점에 사용자에게 D.B 업데이트 알림 전송
        alert11MonthBinderUpdate();
    }

    private void alert11MonthBinderUpdate() {
        List<String> users = cvFinderMapper.findUpdateBinderUsers(330);
        if(!ObjectUtils.isEmpty(users)) {
            log.info("Binder Update 알림 대상자가 존재함. 대상자 수 : {}", users.size());
            Mail mail = new Mail();
            for(String username : users) {
                Account account = userRepository.findByUserId(username);
                mail.setEmail(account.getEmail());

                Context context = new Context();
                context.setVariable("empName", account.getName());
                mailService.send(account.getEmail(), String.format(BinderAlarmType.BINDER_UPDATE.getTitle(), account.getName()), BinderAlarmType.BINDER_UPDATE, context);
            }
        } else {
            log.info("Binder Update 알림 대상자가 없습니다.");
        }
    }
}
