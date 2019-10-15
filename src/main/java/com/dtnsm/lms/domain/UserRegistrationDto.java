package com.dtnsm.lms.domain;

import com.dtnsm.lms.auth.FieldMatch;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;



@FieldMatch.List({
        @FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match"),
        @FieldMatch(first = "templates/email", second = "confirmEmail", message = "The email fields must match")
})
public class UserRegistrationDto {

    @NotEmpty
    private String userId;

    @NotEmpty
    private String name;

    @NotEmpty
    private String password;

    // 사용자 구분 (A:admin, U:일반유저, O:외부유저)
    private String userType;

    @Email
    @NotEmpty
    private String email;

    @AssertTrue
    private Boolean terms;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}