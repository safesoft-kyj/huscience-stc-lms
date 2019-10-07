package com.dtnsm.lms.util;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class UserLogin {

    public static boolean isLogin(String userId, String userPw) throws MalformedURLException {

        // 법인코드(디티앤사노메딕스)
        String domainNum = "4";

        String myURL =  "http://gw.dtnsm.com/checker9_new.aspx?txtDomainNum=%s&txtUserid=%s&txtpassword=%s&sType=LOGIN";

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
}
