package com.dtnsm.lms.controller;

import com.dtnsm.lms.auth.PasswordEncoding;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.component.CourseScheduler;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.CourseManager;
import com.dtnsm.lms.domain.Role;
import com.dtnsm.lms.domain.Schedule;
import com.dtnsm.lms.mybatis.mapper.UserMapper;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.repository.RoleRepository;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.service.CourseManagerService;
import com.dtnsm.lms.service.Mail;
import com.dtnsm.lms.service.MailService;
import com.dtnsm.lms.service.RoleService;
import com.dtnsm.lms.util.DateUtil;
import com.dtnsm.lms.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/registration")
public class RegistrationController {

    @Autowired
    MailService mailService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CourseManagerService courseManagerService;

    @Autowired
    CourseScheduler courseScheduler;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private PasswordEncoding passwordEncoder;

    private PageInfo pageInfo = new PageInfo();

    public RegistrationController() {
        pageInfo.setParentId("m-registration");
        pageInfo.setParentTitle("사용자관리");
    }

//    @ModelAttribute("user")
//    public UserRegistrationDto userRegistrationDto() {
//        return new UserRegistrationDto();
//    }

    @GetMapping("/account/add")
    public String showRegistrationForm(Model model) {

        pageInfo.setPageId("m-registration-add");
        pageInfo.setPageTitle("사용자");

        Account account = new Account();
        account.setUserId("");
        account.setPassword("");
        account.setUserType("O");

        List<Role> roleList = roleService.getList();
        model.addAttribute(pageInfo);
        model.addAttribute("account", account);

        return "admin/registration/account/add";
    }

    @PostMapping("/account/add-post")
    public String registerUserAccount(@Valid Account account
                                        , BindingResult result) {

        if (result.hasErrors()) {
            return "admin/registration/account/add";
        }

        String originPassword = account.getPassword();

        // 초기 패스워드는 저장시 암호화 되며 Role은 사용자 타입에따라 자동 저장 됨
        Account account1 = userService.save(account);

        // 메일보내기(서비스 전까지는 메일 보내지 않음
        Mail mail = new Mail();

        StringBuilder sb = new StringBuilder();
        sb.append("ID:" + account1.getUserId() + "<br>");
        sb.append("PW:" + originPassword + "<br>");
        sb.append("사용자 등록이 완료되었습니다.<br>");
        sb.append("로그인후 패스워드를 변경하세요.<br>");
        sb.append("<a href='http://lms.dtnsm.com'>LMS</a><br>");

        mail.setEmail(account1.getEmail());
        mail.setMessage(sb.toString());
        mail.setObject(account1.getName() + "님 사용자 등록 완료!!!");

        mailService.send(mail);

        return "redirect:/admin/registration/account/list";
    }


    @GetMapping("/account/editRole/{id}")
    public String editRole(@PathVariable("id") String id, Model model) {
        Account account = userService.getAccountByUserId(id);

        pageInfo.setPageId("m-registration-add");
        pageInfo.setPageTitle("권한변경");

        List<Role> roleList = roleService.getList();
        model.addAttribute(pageInfo);
        model.addAttribute("account", account);
        model.addAttribute("roleList", roleList);

        return "admin/registration/account/editRole";
    }


    @PostMapping("/account/editRole-post/{id}")
    public String editRolePost(@PathVariable("id") String id
            , @Valid Account account
            , BindingResult result) {
        if(result.hasErrors()) {
            return "redirect:admin/registration/account/editRole/" + id;
        }

        Account oldAccount = userService.getAccountByUserId(id);
        oldAccount.setRoles(account.getRoles());

        Account saveAccount = userRepository.save(oldAccount);

        return "redirect:/admin/registration/account/list";
    }

    @GetMapping("/account/list")
    public String accountList(Model model) {

        pageInfo.setPageTitle("사용자");

        List<Account> accounts = userService.getAccountList();
        model.addAttribute(pageInfo);
        model.addAttribute("accounts", accounts);

        return "admin/registration/account/list";
    }


    // 그룹웨어 사용자 업데이트
    @GetMapping("/account/gw-user-update")
    public String gwUserUpdate(Model model) {

        pageInfo.setPageTitle("그룹웨어 사용자 업데이트");

        courseScheduler.updateGroupwareUser();
        model.addAttribute(pageInfo);

        return "admin/registration/account/gw-user-update";
    }


    @GetMapping("/account/edit/{id}")
    public String accountUpdate(@PathVariable("id") String id, Model model) {
        Account account = userService.getAccountByUserId(id);

        pageInfo.setPageId("m-customer-edit");
        pageInfo.setPageTitle("Role 수정");
        List<Role> roleList = roleService.getList();

        model.addAttribute(pageInfo);
        model.addAttribute("roleList", roleList);
        model.addAttribute("account", account);
        model.addAttribute("typeList", userService.getTypeList());

        return "admin/registration/account/edit";
    }

    @PostMapping("/account/edit-post/{id}")
    public String accountUpdatePost(@PathVariable("id") String id
            , @Valid Account account
            , BindingResult result) {
        if(result.hasErrors()) {
            return "admin/registration/role/update/" + id;
        }

        Account oldAccount = userService.getAccountByUserId(id);

        if(!account.getPassword().equals(oldAccount.getPassword())) {
            account.setPassword(passwordEncoder.encode(account.getPassword()));
        }

        account.setRoles(oldAccount.getRoles());

        Account saveAccount = userRepository.save(account);

        // 내부직원인 경우 그룹웨어 정보를 업데이트 한다.
        if (saveAccount.getUserType().equals("U")) {
            userService.updateAccountByGroupwareInfo(saveAccount.getUserId());
        }

        return "redirect:/admin/registration/account/list";
    }

    @GetMapping("/account/delete/{id}")
    public String deleteAccount(@PathVariable("id") String id) {

        Account account = userService.getAccountByUserId(id);

        userService.deleteAccount(account);

        return "redirect:/admin/registration/account/list";
    }


    /*
        롤 관리
     */

    @GetMapping("/role/add")
    public String roleAdd(Role role, Model model) {

        pageInfo.setPageId("m-info-month");
        pageInfo.setPageTitle("Role");
        model.addAttribute(pageInfo);


        return "admin/registration/role/add";
    }

    @PostMapping("/role/add-post")
    public String roleAddPost(@Valid Role role, BindingResult result) {

        if(result.hasErrors()) {
            return "admin/registration/role/add";
        }

        roleService.save(role);

        return "redirect:/admin/registration/role/list/";
    }

    @GetMapping("/role/list")
    public String list(Model model) {

        pageInfo.setPageId("m-info-month");
        pageInfo.setPageTitle("Role");

        List<Role> roles = userService.getRoleList();
        model.addAttribute(pageInfo);
        model.addAttribute("roles", roles);

        return "admin/registration/role/list";
    }

    @GetMapping("/role/edit/{id}")
    public String roleUpdate(@PathVariable("id") long id, Model model) {
        Role role = roleService.getRoleById(id);

        pageInfo.setPageId("m-customer-edit");
        pageInfo.setPageTitle("Role");
        model.addAttribute(pageInfo);
        model.addAttribute("role", role);


        return "admin/registration/role/edit";
    }

    @PostMapping("/role/edit-post/{id}")
    public String roleUpdatePost(@PathVariable("id") long id, @Valid Role role, BindingResult result) {
        if(result.hasErrors()) {
            role.setId(id);
            return "content/registration/role/edit" + id;
        }

        roleService.save(role);

        return "redirect:/admin/registration/role/list";
    }

    @GetMapping("/role/delete/{id}")
    public String deleteRole(@PathVariable("id") long id) {

        Role role = roleService.getRoleById(id);

        roleService.delete(role);

        return "redirect:/admin/registration/role/list";
    }

    /*
        사용자별 Role
     */

    @GetMapping("/accountRole/list")
    public String accountRoleList(Model model) {

        pageInfo.setPageId("m-info-month");
        pageInfo.setPageTitle("사용자별 Role");

        List<Account> accounts = userService.getAccountList();
        model.addAttribute(pageInfo);
        model.addAttribute("accounts", accounts);

        return "admin/registration/accountRole/list";
    }


     /*
        과정 관리자 등록
     */


    @GetMapping("/courseManager/add")
    public String courseManagerAdd(CourseManager courseManager, Model model) {


        pageInfo.setPageId("m-info-month");
        pageInfo.setPageTitle("교육과정관리자");

        model.addAttribute(pageInfo);
        model.addAttribute("accountList", userService.getAccountList());
        model.addAttribute("courseManager", courseManager);

        return "admin/registration/courseManager/add";
    }

    @PostMapping("/courseManager/add-post")
    public String courseManagerAddPost(@Valid CourseManager courseManager, BindingResult result) {

        if(result.hasErrors()) {
            return "admin/registration/courseManager/add";
        }

        courseManagerService.save(courseManager);

        return "redirect:/admin/registration/courseManager/list/";
    }


    @GetMapping("/courseManager/edit/{userId}")
    public String courseManagerUpdate(@PathVariable("userId") String userId, Model model) {
        CourseManager courseManager = courseManagerService.getByUserId(userId);

        pageInfo.setPageId("m-customer-edit");
        pageInfo.setPageTitle("교육과정관리자");
        model.addAttribute(pageInfo);
        model.addAttribute("accountList", userService.getAccountList());
        model.addAttribute("courseManager", courseManager);


        return "admin/registration/courseManager/edit";
    }

    @GetMapping("/courseManager/updateActive/{userId}")
    public String updateActive(@PathVariable("userId") String userId) {

        // 모든 설문을 초기화 한다.
        for(CourseManager courseManager : courseManagerService.getList()) {
            courseManager.setIsActive(0);
            courseManagerService.save(courseManager);
        }

        // 요청된 설문을 기본 설문으로 변경한다.
        CourseManager courseManager = courseManagerService.getByUserId(userId);
        courseManager.setIsActive(1);
        courseManagerService.save(courseManager);

        return "redirect:/admin/registration/courseManager/list/";
    }

    @PostMapping("/courseManager/edit-post/{userId}")
    public String courseManagerUpdatePost(@PathVariable("userId") String userId, @Valid CourseManager courseManager, BindingResult result) {
        if(result.hasErrors()) {
            courseManager.setAccount(courseManager.getAccount());
            return "content/registration/courseManager/edit" + userId;
        }

        CourseManager oldCourseManager = courseManagerService.getByUserId(userId);
        courseManager.setIsActive(oldCourseManager.getIsActive());
        courseManagerService.save(courseManager);

        return "redirect:/admin/registration/courseManager/list";
    }

    @GetMapping("/courseManager/list")
    public String courseManagerList(Model model) {

        pageInfo.setPageId("m-info-month");
        pageInfo.setPageTitle("교육과정관리자");

        List<CourseManager> managers = courseManagerService.getList();

        model.addAttribute(pageInfo);
        model.addAttribute("managers", managers);

        return "admin/registration/courseManager/list";
    }


    @GetMapping("/courseManager/delete/{userId}")
    public String deleteRole(@PathVariable("userId") String userId) {

        CourseManager courseManager = courseManagerService.getByUserId(userId);

        courseManagerService.delete(courseManager);

        return "redirect:/admin/registration/courseManager/list";
    }
}
