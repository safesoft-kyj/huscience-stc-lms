package com.dtnsm.lms.util;


import com.dtnsm.lms.auth.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class SessionUtil {

    public static CustomUserDetails getUserDetail() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userDetails;
    }

    public static String getUserId() {

        return getUserDetail().getUserId();
    }
}
