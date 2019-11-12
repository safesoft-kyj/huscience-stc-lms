package com.dtnsm.lms.util;


import com.dtnsm.lms.auth.CustomUserDetails;
import com.dtnsm.lms.auth.UserServiceImpl;
import org.springframework.security.core.context.SecurityContextHolder;

public class SessionUtil {

    public static UserServiceImpl userService = new UserServiceImpl();

    public static CustomUserDetails getUserDetail() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userDetails;
    }

    public static String getUserId() {

        return getUserDetail().getUserId();
    }


    public static String getUserName(String userId) {


        return SessionUtil.userService.getAccountByUserId(userId).getName();
    }

    public static String getUserEngName(String userId) {

        UserServiceImpl userService = new UserServiceImpl();

        return SessionUtil.userService.getAccountByUserId(userId).getEngName();
    }
}
