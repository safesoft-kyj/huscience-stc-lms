package com.dtnsm.lms.component;

import com.dtnsm.lms.auth.CustomUserDetails;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.LoginHistory;
import com.dtnsm.lms.repository.LoginHistoryRepository;
import com.dtnsm.lms.util.UserLogin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author ks.hwnag@safesoft.co.kr
 * @version 1.0.0
 * @sine 2020-04-20 오후 1:24
 * @desc Github : https://github.com/pub147
 **/
@Slf4j
@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    LoginHistoryRepository loginHistoryRepository;

    private RequestCache requestCache = new HttpSessionRequestCache();

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        SavedRequest savedRequest = requestCache.getRequest(request, response);

//        log.info("로그인 성공");
        CustomUserDetails account = (CustomUserDetails)authentication.getPrincipal();

        // Login 이력
        if (account != null) {
//            HttpSession session = request.getSession(true);

            LoginHistory loginHistory = new LoginHistory();
            loginHistory.setUserId(account.getUserId());
            loginHistory.setName(account.getUsername());
            loginHistory.setEngName(account.getEngName());
            loginHistory.setIpAddress(UserLogin.getClientIP(request));
//            loginHistory.setSessionId(session.getId());
            loginHistory = loginHistoryRepository.save(loginHistory);

            account.setLoginSessionId(loginHistory.getId());
        }

        if(savedRequest == null){
//            log.info("메인페이지로 이동");
            response.sendRedirect("/");   //로그인 하기 전의 페이지가 없었다면 이주소로 이동
        }else{
//            log.info("로그인 하기전의 접속 주소로 이동");
            super.onAuthenticationSuccess(request, response, authentication);    //로그인 하기 전의 접속한 주소로 다시 돌아갑니다.
        }
    }
}