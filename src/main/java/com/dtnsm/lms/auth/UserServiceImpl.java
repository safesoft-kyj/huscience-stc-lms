package com.dtnsm.lms.auth;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.dtnsm.lms.mybatis.dto.UserVO;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserMapperService userMapperService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Account findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    public Account save(UserRegistrationDto registration) {
        Account user = new Account();
        user.setUserId(registration.getUserId());
        user.setName(registration.getName());
        user.setEmail(registration.getEmail());
        user.setPassword(passwordEncoder.encode(registration.getPassword()));
        user.setRoles(Arrays.asList(new Role("ROLE_USER")));
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        Account user = userRepository.findByUserId(userId);

        if (user == null) {
            UserVO userVO = userMapperService.getUserById(userId);
            Role role = roleRepository.findByName("ROLE_USER");

            Account newUser = new Account();
            newUser.setUserId(userId);
            newUser.setName(userVO.getKorName());
            newUser.setPassword(userVO.getPassword());
            newUser.setEmail(userVO.getEmail());
            newUser.setRoles(Arrays.asList(role));
            newUser.setEnabled(true);
            user = userRepository.save(newUser);
        }

        logger.info("로그시작");
        logger.info(user.getUserId());

        CustomUserDetails userDetails = null;

        if(user != null) {
            userDetails = new CustomUserDetails();
            userDetails.setUser(user);
        } else {
            throw new UsernameNotFoundException("User not exist with name : " + userId);
        }


        return userDetails;

//        return new org.springframework.security.core.userdetails.User(user.getUserId(),
//                user.getPassword(),
//                mapRolesToAuthorities(user.getRoles()));
    }

    public UserDetails loadUserByUsername(String userId, String password) throws UsernameNotFoundException {

        Account user = userRepository.findByUserId(userId);
        CustomUserDetails userDetails = null;

        if(user != null) {
            userDetails = new CustomUserDetails();
            userDetails.setUser(user);
        } else {
            throw new UsernameNotFoundException("User not exist with name : " + userId);
        }

        return userDetails;

//        return new org.springframework.security.core.userdetails.User(user.getUserId(),
//                user.getPassword(),
//                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection < ? extends GrantedAuthority > mapRolesToAuthorities(Collection < Role > roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}