package com.dtnsm.lms.auth;

import com.dtnsm.lms.domain.LoginHistory;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.repository.RoleRepository;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.util.UserLogin;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.net.MalformedURLException;

@Slf4j
@Component
public class UserAuthenticationProvider implements AuthenticationProvider {


//    @Autowired
//    UserService userService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserMapperService userMapperService;

    @Autowired
    private UserRepository userRepository;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String userId = authentication.getName();
        String password = (String) authentication.getCredentials();

        //그룹웨어 로그인 체크
        boolean isLogin = false;
        try {
            //TODO Test code
//            if(("hjlim".equals(userId) || "mjlee".equals(userId)) && "1".equals(password)) {
//                isLogin = true;
//            } else {
//                isLogin = UserLogin.isLogin(userId, password);
//            }
            // 혹시 그룹웨어에 admin 계정을 있을경우 대비해서 제외시킴
            if (!userId.equals("admin")) {
                isLogin = UserLogin.isLogin(userId, password);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        CustomUserDetails account;

        // 외부사용자 로그인 체크
        if (isLogin) {
//            log.info("@userId : {}, isLogin : {}", userId, isLogin);
            account = (CustomUserDetails) userService.loadUserByUsername(userId);

        } else {
            account = (CustomUserDetails) userService.loadUserByUsername(userId, password);

            // 외부사용 사용유무가 flase면 로그인을 못하게 막는다.
            if (!account.isEnabled()) account = null;
        }

        if(!ObjectUtils.isEmpty(account)) {
//            log.info("==> 로그인한 사용자를 매니저로 선택한 사용자 리스트 조회 : {}", userId);
            //로그인한 사용자를 매니저로 선택한 사용자 리스트 조회
            account.setManager(userService.findByParentUserId(userId).isPresent());
//            log.info("==> 로그인한 사용자를 매니저로 선택한 사용자 리스트 조회 : {}, 결과 : {}", userId, account.isManager());
        }

        if (account == null) {
            return null;
        }

        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());

        return result;

    }

    @Override
    public boolean supports(Class authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }



}