package com.dtnsm.lms.domain;

import com.dtnsm.common.entity.Signature;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
//@Table(uniqueConstraints = @UniqueConstraint(columnNames = "userId"))
public class Account {


//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
    @Id
    @Column(length = 30)
    private String userId;

    private String name;


    private String engName;
    private String email;
    private String password;

    // 사번
    @Column(length = 100)
    private String comNum;
    // 부서
    private String orgDepart;
    // 업무
    private String comJob;
    // 직위
    private String comPosition;
    // 입사일
    private String indate;

    // 상위 결재권자
    @Column(length = 30)
    @ColumnDefault(value="''")
    private String parentUserId=" ";

    // 사용자 구분 (A:admin, U:일반유저, O:외부유저)
    private String userType;

    // 사용 유무
    private boolean enabled;
    private boolean tokenExpired;

    @OneToOne(mappedBy="account")
    @PrimaryKeyJoinColumn
    private CourseManager courseManager;

//    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<CourseAccount> courseAccounts = new ArrayList<>();

//    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "account_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "userId"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

//    @OneToMany(mappedBy = "account")
//    private List<BinderCv> binderCvList;

//    @OneToMany(mappedBy = "account")
//    private List<BinderJd> binderJdList;

    @OneToMany(mappedBy = "account")
    private List<CourseAccount> courseAccountList;

//    @OneToMany(mappedBy = "document")
//    private List<DocumentAccount> documentAccountList;

    public Account() {}

    public Account(String userId, String name, String email, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Account(String userId, String name, String email, String password, Collection < Role > roles) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public boolean addCourseAccount(CourseAccount courseAccount) {
        if(courseAccountList == null)
            courseAccountList = new ArrayList<>();

        return courseAccountList.add(courseAccount);
    }

//    public boolean addDocumentAccount(DocumentAccount documentAccount) {
//        if(documentAccountList == null)
//            documentAccountList = new ArrayList<>();
//
//        return documentAccountList.add(documentAccount);
//    }

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

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

    public String getParentUserId() {
        return parentUserId;
    }

    public void setParentUserId(String parentUserId) {
        this.parentUserId = parentUserId;
    }

    public void setEngName(String engName) {
        this.engName = engName;
    }

    public String getEngName() {
        return engName;
    }

    public void setComNum(String comNum) {
        this.comNum = comNum;
    }

    public String getComNum() {
        return comNum;
    }

    public String getOrgDepart() {
        return orgDepart;
    }

    public String getComJob() {
        return comJob;
    }

    public String getComPosition() {
        return comPosition;
    }

    public String getIndate() {
        return indate;
    }

    public void setOrgDepart(String orgDepart) {
        this.orgDepart = orgDepart;
    }

    public void setComJob(String comJob) {
        this.comJob = comJob;
    }

    public void setComPosition(String comPosition) {
        this.comPosition = comPosition;
    }

    public void setIndate(String indate) {
        this.indate = indate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public boolean getTokenExpired() {
        return tokenExpired;
    }

    public void setTokenExpired(boolean tokenExpired) {
        this.tokenExpired = tokenExpired;
    }

    public Collection < Role > getRoles() {
        return roles;
    }

    public void setRoles(Collection < Role > roles) {
        this.roles = roles;
    }



    @Override
    public String toString() {
        return "User{" +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + "*********" + '\'' +
                ", roles=" + roles +
                '}';
    }
}