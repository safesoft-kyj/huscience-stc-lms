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
        CustomUserDetails account = null;

        String userId = authentication.getName();
        String password = (String) authentication.getCredentials();

        Object credentials = authentication.getCredentials();

        //그룹웨어 로그인 체크
        boolean isLogin = false;

        //01. 임시 관리자권한으로 로그인하기(개발자 로그인 모드)
        isLogin = isLoginDevelopment(userId, password);
        if(isLogin)
            userId = userId.substring(0,userId.indexOf("#admin"));

        //02. 그룹웨어 사용자인지 체크하기
        if(isLogin == false) isLogin = isLoginGroupWare(userId, password);

        //03. 사용자 계정 가져오기 및 manager인지 여부 체크(로그인 처리가 된 경우 UserId로 검색, 로그인 성공이되지 않았다면 외부 사용자로 ID,PASSWORD로 검색)
        account = getCustomUserDetails(userId, password, isLogin);

        if (account == null) {
            return null;
        } else {
            if (!account.isEnabled())
                return null;
        }

        //04. UsernamePasswordAuthenticationToken 반환
        UsernamePasswordAuthenticationToken authenticationToken = null;
        if (account != null) {
            //05 로그인한 사용자를 매니저로 선택한 사용자 리스트 조회
            account.setManager(userService.findByParentUserId(userId).isPresent());

            authenticationToken = new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());
        }

        return authenticationToken;
//
//        boolean isAdmin = userId.endsWith("#admin");
//        try {
//            //TODO Test code
////            if(("hjlim".equals(userId) || "sjlee".equals(userId) || "yjlee".equals(userId)) && "1".equals(password)) {
////                isLogin = true;
////            } else
////            } else {
////                isLogin = UserLogin.isLogin(userId, password);
////            }
//            // 혹시 그룹웨어에 admin 계정을 있을경우 대비해서 제외시킴
//
//            if(isAdmin && "admin!!".equals(credentials)) {
//                log.info("@관리자 로그인({})", userId);
//                isLogin = true;
//                userId = userId.substring(0, userId.indexOf("#"));
//            }
//            else{
//                isLogin = UserLogin.isLogin(userId, password);
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//
//        CustomUserDetails account;
//
//        // 외부사용자 로그인 체크
//        if (isLogin) {
////            log.info("@userId : {}, isLogin : {}", userId, isLogin);
//            account = (CustomUserDetails) userService.loadUserByUsername(userId);
//        }else if(isAdmin && "admin!!".equals(password)){
//            userId = userId.substring(0, userId.indexOf("#"));
//            account = (CustomUserDetails) userService.loadUserByUsername(userId);
//        } else {
//            account = (CustomUserDetails) userService.loadUserByUsername(userId, password);
//        }
//
//        if(!ObjectUtils.isEmpty(account)) {
////            log.info("==> 로그인한 사용자를 매니저로 선택한 사용자 리스트 조회 : {}", userId);
//            //로그인한 사용자를 매니저로 선택한 사용자 리스트 조회
//            account.setManager(userService.findByParentUserId(userId).isPresent());
////            log.info("==> 로그인한 사용자를 매니저로 선택한 사용자 리스트 조회 : {}, 결과 : {}", userId, account.isManager());
//        }
//
//        if (account == null) {
//            return null;
//        } else {
//            // enabled가 false면 로그인을 못하게 막는다.
//            if (!account.isEnabled())
//                return null;
//        }
//
//        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());
//
//        return result;

    }

    @Override
    public boolean supports(Class authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    /**
     *
     * @param userId
     * @param password
     * @param isLogin : 이미 로그인 체크가 되었는지 여부(체크가 되지 않았다면 외부 사용자인 경우다.)
     * @return CustomerUserDetails 정보
     */
    private CustomUserDetails getCustomUserDetails(String userId, String password, boolean isLogin) {
        CustomUserDetails account = null;
        if (isLogin) {
            account = (CustomUserDetails) userService.loadUserByUsername(userId);
        } else {
            account = (CustomUserDetails) userService.loadUserByUsername(userId, password);
            if (!account.isEnabled())
                account = null;
        }

        return account;
    }

    /**
     * 그룹웨어 사용자인지 체크하기
     * @param userId
     * @param password
     * @return
     */
    private boolean isLoginGroupWare(String userId, String password) {
        Boolean isLogin = false;
        try {
            if (!userId.equals("admin")) {
                //그룹웨어에 사용가 있는지 물어본다.
                isLogin = UserLogin.isLogin(userId, password);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return isLogin;
    }

    /**
     * 임시 관리자권한으로 로그인하기(개발자 로그인 모드)
     * @param userId
     * @param password
     * @return
     */
    private boolean isLoginDevelopment(String userId, String password) {
        boolean isLogin = false;
        if(userId.endsWith("#admin") && password.equals("admin!!")) {
            isLogin = true;
            log.debug("관리자 로그인 : {}", isLogin);
        }
        return isLogin;
    }

}