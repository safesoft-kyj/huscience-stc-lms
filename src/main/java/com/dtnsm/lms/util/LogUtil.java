package com.dtnsm.lms.util;

import com.dtnsm.lms.domain.LmsLog;
import com.dtnsm.lms.repository.LmsLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LogUtil {

    private static LmsLogRepository logRepository;

    @Autowired
    private LogUtil(LmsLogRepository logRepository){
        this.logRepository = logRepository;
    }

    // 메세지 전송(알람 및 메일)
    public static void write(String formName, String lvl, String message) {

        LmsLog log = new LmsLog();
        log.setFormName(formName);
        log.setLvl(lvl);
        log.setMessage(message);

        logRepository.save(log);
    }
}
