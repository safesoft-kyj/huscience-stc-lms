package com.dtnsm.lms.component;

import com.dtnsm.lms.auth.CustomUserDetails;
import com.dtnsm.lms.domain.LoginHistory;
import com.dtnsm.lms.repository.LoginHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

/**
 * @author ks.hwnag@safesoft.co.kr
 * @version 1.0.0
 * @sine 2020-04-20 오후 1:24
 * @desc Github : https://github.com/pub147
 **/
@Slf4j
@Component
public class LogoutCustomSuccessHandler implements LogoutSuccessHandler {
    @Autowired
    LoginHistoryRepository loginHistoryRepository;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {


        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

//        log.info("====log out session=== : {}", customUserDetails.getLoginSessionId());

        if (authentication != null && authentication.getDetails() != null) {
            try {

                Optional<LoginHistory> loginHistory = loginHistoryRepository.findById(customUserDetails.getLoginSessionId());

                if(loginHistory.isPresent()) {
                    LoginHistory loginHistory1 = loginHistory.get();
                    loginHistory1.setLogoutDateTime(new Date());
                    loginHistoryRepository.save(loginHistory1);
                }

                request.getSession().invalidate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.sendRedirect("/login?logout");
    }
}