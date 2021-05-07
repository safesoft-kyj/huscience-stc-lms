package com.dtnsm.lms.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;



@Slf4j
@Component
public class UserLogin {

    private static String domainNum;

    @Value("${gw.code}")
    public void setDomainNum(String value) {
        domainNum = value;
    }

    public static boolean isLogin(String userId, String userPw) throws MalformedURLException {

        String myURL =  "http://gw.dtnsm.com/checker9_new.aspx?txtDomainNum=%s&txtUserid=%s&txtpassword=%s&sType=LOGIN";

        try {
            userPw = URLEncoder.encode(userPw, "UTF-8");
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        // 법인코드/사용자아이디/패스워드
        myURL = String.format(myURL, domainNum, userId, userPw);

        System.out.println("Requeted URL:" + myURL);
        StringBuilder sb = new StringBuilder();
        URLConnection urlConn = null;
        InputStreamReader in = null;

        //error : Caused by: javax.net.ssl.SSLPeerUnverifiedException: Hostname not verified:
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {

                return true;
            }
        };
        //
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        try {
            URL url = new URL(myURL);
            urlConn = url.openConnection();
            if (urlConn != null)
                urlConn.setReadTimeout(60 * 1000);
            if (urlConn != null && urlConn.getInputStream() != null) {
                in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
                //charset 문자 집합의 인코딩을 사용해 urlConn.getInputStream을 문자스트림으로 변환 객체를 생성.
                BufferedReader bufferedReader = new BufferedReader(in);
                //주어진 문자 입력 스트림 inputStream에 대해 기본 크기의 버퍼를 갖는 객체를 생성.
                if (bufferedReader != null) {
                    int cp;
                    while ((cp = bufferedReader.read()) != -1) {
                        sb.append((char) cp);
                    }
                    bufferedReader.close();
                }
            }
            in.close();
        } catch (Exception e) {
            throw new RuntimeException("Exception URL:"+ myURL, e);
        }

        if (sb.toString().equals("Y")) return true;
        else return false;

    }

    public static String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        log.info("> X-FORWARDED-FOR : " + ip);

        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
            log.info("> Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            log.info(">  WL-Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            log.info("> HTTP_CLIENT_IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            log.info("> HTTP_X_FORWARDED_FOR : " + ip);
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
            log.info("> getRemoteAddr : "+ip);
        }
        log.info("> Result : IP Address : "+ip);

        return ip;
    }
}
