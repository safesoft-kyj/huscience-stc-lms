package com.dtnsm.lms.controller;

import javax.validation.Valid;

import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.Role;
import com.dtnsm.lms.service.Mail;
import com.dtnsm.lms.service.MailService;
import com.dtnsm.lms.service.RoleService;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/registration")
public class RegistrationController {

    @Autowired
    MailService mailService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RoleService roleService;

    private PageInfo pageInfo = new PageInfo();

    public RegistrationController() {
        pageInfo.setParentId("m-registration");
        pageInfo.setParentTitle("외부사용자등록");
    }

//    @ModelAttribute("user")
//    public UserRegistrationDto userRegistrationDto() {
//        return new UserRegistrationDto();
//    }

    @GetMapping("/account/add")
    public String showRegistrationForm(Account account, Model model) {

        pageInfo.setPageId("m-registration-add");
        pageInfo.setPageTitle("사용자등록");

        List<Role> roleList = roleService.getList();
        model.addAttribute(pageInfo);
        model.addAttribute("roleList", roleList);
        model.addAttribute("typeList", userService.getTypeList());

        return "admin/registration/account/add";
    }

    @PostMapping("/account/add-post")
    public String registerUserAccount(@Valid Account account
                                        , BindingResult result) {


        Account existing = userService.findByUserId(account.getUserId());
        if (existing != null) {
            result.rejectValue("userId", null, "There is already an account registered with that email");
        }

        if (result.hasErrors()) {
            return "admin/registration/account/add";
        }

        String originPassword = account.getPassword();

        Account account1 = userService.save(account);

        // 메일보내기(서비스 전까지는 메일 보내지 않음
        Mail mail = new Mail();

        StringBuilder sb = new StringBuilder();
        sb.append("ID:" + account1.getUserId() + "<br>");
        sb.append("PW:" + originPassword + "<br>");
        sb.append("사용자 등록이 완료되었습니다.<br>");
        sb.append("로그인후 패스워드를 변경하세요.<br>");
        sb.append("<a href='http://localhost:8080/'>홈페이지</a><br>");

        mail.setEmail(account1.getEmail());
        mail.setMessage(sb.toString());
        mail.setObject(account1.getName() + "님 사용자 등록 완료!!!");

        mailService.send(mail);

        //return "redirect:/registration?success";
        return "redirect:/home";
    }

    @GetMapping("/account/list")
    public String accountList(Model model) {

        pageInfo.setPageTitle("사용자 조회");

        List<Account> accounts = userService.getAccountList();
        model.addAttribute(pageInfo);
        model.addAttribute("accounts", accounts);

        return "admin/registration/account/list";
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

        userService.save(account);

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
        pageInfo.setPageTitle("Role 추가");
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
        pageInfo.setPageTitle("Role 조회");

        List<Role> roles = userService.getRoleList();
        model.addAttribute(pageInfo);
        model.addAttribute("roles", roles);

        return "admin/registration/role/list";
    }

    @GetMapping("/role/edit/{id}")
    public String roleUpdate(@PathVariable("id") long id, Model model) {
        Role role = roleService.getRoleById(id);

        pageInfo.setPageId("m-customer-edit");
        pageInfo.setPageTitle("Role 수정");
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
        pageInfo.setPageTitle("사용자별 Role 조회");

        List<Account> accounts = userService.getAccountList();
        model.addAttribute(pageInfo);
        model.addAttribute("accounts", accounts);

        return "admin/registration/accountRole/list";
    }
}
