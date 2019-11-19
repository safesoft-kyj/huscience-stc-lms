package com.dtnsm.lms.auth;

import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.repository.RoleRepository;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.util.UserLogin;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;

@Slf4j
@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    Logger logger = LoggerFactory.getLogger(UserAuthenticationProvider.class);


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
            isLogin = UserLogin.isLogin(userId, password);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        CustomUserDetails account;

        // 외부사용자 로그인 체크
        if (isLogin) {
            account = (CustomUserDetails) userService.loadUserByUsername(userId);
            //query..........
            account.setManager(true);
        } else {
            account = (CustomUserDetails) userService.loadUserByUsername(userId, password);
        }

        if (account == null) {
            return null;
        }





//        PasswordEncoding encoding = new PasswordEncoding();
//        if (!encoding.matches(account.getPassword(), password)) return null;



//        String confrimPassword = passwordEncoder.encode(password);

//        if (!account.getPassword().equals(confrimPassword)) return null;



//        String roleName = "";
//        for(Role role : user.getRoles()) {
//            roleName = role.getName();
//        }
//
//        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority(roleName));


        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());

        return result;
    }

    @Override
    public boolean supports(Class authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }



}