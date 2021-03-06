package com.dtnsm.lms.controller;

import com.dtnsm.common.entity.Signature;
import com.dtnsm.common.repository.SignatureRepository;
import com.dtnsm.common.repository.UserJobDescriptionRepository;
import com.dtnsm.lms.auth.PasswordEncoding;
import com.dtnsm.lms.auth.UserServiceImpl;
import com.dtnsm.lms.component.CourseScheduler;
import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.CourseManager;
import com.dtnsm.lms.domain.Role;
import com.dtnsm.lms.repository.RoleRepository;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.service.CourseManagerService;
import com.dtnsm.lms.service.Mail;
import com.dtnsm.lms.service.MailService;
import com.dtnsm.lms.service.RoleService;
import com.dtnsm.lms.util.PageInfo;
import org.apache.xmlbeans.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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
    private SignatureRepository signatureRepository;

    @Autowired
    private PasswordEncoding passwordEncoder;

    @Autowired
    private UserJobDescriptionRepository userJobDescriptionRepository;

    private PageInfo pageInfo = new PageInfo();

    public RegistrationController() {
        pageInfo.setParentId("m-registration");
        pageInfo.setParentTitle("???????????????");
    }

//    @ModelAttribute("user")
//    public UserRegistrationDto userRegistrationDto() {
//        return new UserRegistrationDto();
//    }

    @GetMapping("/account/add")
    public String showRegistrationUserForm(Model model) {

        pageInfo.setPageId("m-registration-add");
        pageInfo.setPageTitle("?????????");

        Account account = new Account();
        account.setUserId("");
        account.setPassword("");
        account.setUserType("U");

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

        // 1.?????? ???????????? ?????????????????? ????????????????????? ????????????.
//        if(account.getUserType().equals("O")){
//            if(!ObjectUtils.isEmpty(courseManagerService.getCourseManager())) {
//                account.setParentUserId(courseManagerService.getCourseManager().getAccount().getUserId());
//            } else {
//                //????????????????????? ????????? ?????????. ?????? ??????????????? ??????
//            }
//        }



        // ?????? ??????????????? ????????? ????????? ?????? Role??? ????????? ??????????????? ?????? ?????? ???
        Account account1 = userService.save(account);

//        // 2. ?????? ????????? ??????????????? ?????? ??????
//        if(account.getUserType().equals("O")) {
//            Optional<Signature> optionalSignature = signatureRepository.findById(account1.getUserId());
//            if (!optionalSignature.isPresent()) {
//                Signature signature = new Signature();
//                signature.setId(account1.getUserId());
//                signature.setBase64signature("");
//                signatureRepository.save(signature);
//            }
//        }

        // ???????????????(????????? ???????????? ?????? ????????? ??????
        Mail mail = new Mail();

        StringBuilder sb = new StringBuilder();
        sb.append("ID : " + account1.getUserId() + "<br>");
        sb.append("PW : " + originPassword + "<br>");
        sb.append("????????? ????????? ?????????????????????.");
//        sb.append("???????????? ??????????????? ???????????????.<br>");
//        sb.append("<a href='http://lms.dtnsm.com'>LMS</a><br>");

        mail.setEmail(account1.getEmail());
        mail.setMessage(sb.toString());
        mail.setObject("[LMS/?????????] " + account1.getName() + "??? ????????? ?????? ??????");

        mailService.sendAccount(mail);

        return "redirect:/admin/registration/account";
    }


    @GetMapping("/account/editRole/{id}")
    public String editRole(@PathVariable("id") String id, Model model) {
        Account account = userService.getAccountByUserId(id);

        pageInfo.setPageId("m-registration-add");
        pageInfo.setPageTitle("????????????");

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

        return "redirect:/admin/registration/account";
    }

    @GetMapping("/account")
    public String accountList(Model model) {

        pageInfo.setPageTitle("?????????");

        List<Account> accounts = userService.getAccountList();
        model.addAttribute(pageInfo);
        model.addAttribute("accounts", accounts);

        return "admin/registration/account/list";
    }


    // ???????????? ????????? ????????????
    @GetMapping("/account/gw-user-update")
    public String gwUserUpdate(Model model) {

        pageInfo.setPageTitle("???????????? ????????? ????????????");

        courseScheduler.updateGroupwareUser();
        model.addAttribute(pageInfo);

        return "admin/registration/account/gw-user-update";
    }


    @GetMapping("/account/edit/{id}")
    public String accountUpdate(@PathVariable("id") String id, Model model) {
        Account account = userService.getAccountByUserId(id);

        pageInfo.setPageId("m-customer-edit");
        pageInfo.setPageTitle("?????????");
        List<Role> roleList = roleService.getList();

        // ?????? ????????? ????????? ??????.
//        Optional<Signature> optionalSignature = signatureRepository.findById(SessionUtil.getUserId());
//        String sign = optionalSignature.isPresent() ? optionalSignature.get().getBase64signature() : "";

        model.addAttribute(pageInfo);
        model.addAttribute("roleList", roleList);
        model.addAttribute("account", account);
        model.addAttribute("typeList", userService.getTypeList());
//        model.addAttribute("accountList", userService.getAccountList());
//        model.addAttribute("sign", sign);

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
        account.setParentUserId(oldAccount.getParentUserId());

        // 1.?????? ????????? ????????????????????? ???????????? ?????? ?????? ?????? ?????????????????? ????????????.
        if (account.getParentUserId() == null || account.getParentUserId().isEmpty()) {
            if(courseManagerService.getCourseManager().getAccount().getUserId().isEmpty())
                return "redirect:/admin/registration/account";
            else
                account.setParentUserId(courseManagerService.getCourseManager().getAccount().getUserId());
        }
        // ????????????.
        Account saveAccount = userRepository.save(account);

        // 2. ?????? ????????? ????????? ???????????? ?????? ????????? ?????? ??????
        Optional<Signature> optionalSignature = signatureRepository.findById(saveAccount.getUserId());
        if(!optionalSignature.isPresent()) {
            Signature signature = new Signature();
            signature.setId(saveAccount.getUserId());
            signature.setBase64signature("");
            signatureRepository.save(signature);
        }

        //TODO ???????????? ?????? ??????
        // ??????????????? ?????? ???????????? ????????? ???????????? ??????.
//        if (saveAccount.getUserType().equals("U")) {
//            userService.updateAccountByGroupwareInfo(saveAccount.getUserId());
//        }

        return "redirect:/admin/registration/account";
    }

    @GetMapping("/account/delete/{id}")
    public String deleteAccount(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {

        Account account = userService.getAccountByUserId(id);
        if(account.getName().contains("??????")){
            account.setEnabled(true);
            account.setName(account.getName().replaceAll("_??????",""));
            userRepository.save(account);
            redirectAttributes.addFlashAttribute("message","???????????????????????????.");
            return "redirect:/admin/registration/account";
        }
        //???????????? ??? ??? ????????? ????????? ?????? ??????.
        Optional<Signature> signature = signatureRepository.findById(id);
        if (signature.isPresent()) {
            signatureRepository.deleteById(id);
        }
        account.setEnabled(false);
        account.setName(account.getName()+"_??????");
        userRepository.save(account);
        redirectAttributes.addFlashAttribute("message","???????????????????????????.");
        //???????????? ??? ??? ????????? ?????? ???????????? ?????? ??????.
        /*userJobDescriptionRepository.deleteByUsername(id);*/
        /*userService.deleteAccount(account);*/
        return "redirect:/admin/registration/account";
    }


    /*
        ??? ??????
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

        return "redirect:/admin/registration/role";
    }

    @GetMapping("/role")
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

        return "redirect:/admin/registration/role";
    }

    @GetMapping("/role/delete/{id}")
    public String deleteRole(@PathVariable("id") long id) {

        Role role = roleService.getRoleById(id);

        roleService.delete(role);

        return "redirect:/admin/registration/role";
    }

    /*
        ???????????? Role
     */

    @GetMapping("/accountRole/list")
    public String accountRoleList(Model model) {


        pageInfo.setPageTitle("???????????? Role");

        List<Account> accounts = userService.getAccountList();
        model.addAttribute(pageInfo);
        model.addAttribute("accounts", accounts);

        return "admin/registration/accountRole/list";
    }


     /*
        ?????? ????????? ??????
     */


    @GetMapping("/courseManager/add")
    public String courseManagerAdd(CourseManager courseManager, Model model) {

        pageInfo.setParentTitle("????????????????????????");
        pageInfo.setPageTitle("?????????????????????");

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

        return "redirect:/admin/registration/courseManager";
    }


    @GetMapping("/courseManager/edit")
    public String courseManagerUpdate(@RequestParam("userId") String userId, Model model) {
        CourseManager courseManager = courseManagerService.getByUserId(userId);

        pageInfo.setParentTitle("????????????????????????");
        pageInfo.setPageTitle("?????????????????????");
        model.addAttribute(pageInfo);
        model.addAttribute("accountList", userService.getAccountList());
        model.addAttribute("courseManager", courseManager);


        return "admin/registration/courseManager/edit";
    }

    @GetMapping("/courseManager/updateActive")
    public String updateActive(@RequestParam("userId") String userId) {

        // ?????? ????????? ????????? ??????.
        for(CourseManager courseManager : courseManagerService.getList()) {
            courseManager.setIsActive(0);
            courseManagerService.save(courseManager);
        }

        // ????????? ????????? ?????? ???????????? ????????????.
        CourseManager courseManager = courseManagerService.getByUserId(userId);
        courseManager.setIsActive(1);
        courseManagerService.save(courseManager);

        return "redirect:/admin/registration/courseManager";
    }

    @PostMapping("/courseManager/edit-post/{userId}")
    public String courseManagerUpdatePost(@PathVariable("userId") String userId, @Valid CourseManager courseManager, BindingResult result) {
        if(result.hasErrors()) {
            courseManager.setAccount(courseManager.getAccount());
            return "admin/registration/courseManager/edit";
        }

        CourseManager oldCourseManager = courseManagerService.getByUserId(userId);
        courseManager.setIsActive(oldCourseManager.getIsActive());
        courseManagerService.save(courseManager);

        return "redirect:/admin/registration/courseManager";
    }

    @GetMapping("/courseManager")
    public String courseManagerList(Model model) {

        pageInfo.setParentTitle("????????????????????????");
        pageInfo.setPageTitle("?????????????????????");

        List<CourseManager> managers = courseManagerService.getList();

        model.addAttribute(pageInfo);
        model.addAttribute("managers", managers);

        return "admin/registration/courseManager/list";
    }


    @GetMapping("/courseManager/delete")
    public String deleteRole(@RequestParam("userId") String userId, RedirectAttributes attributes) {

        List<CourseManager> courseManagerList = courseManagerService.getList();
        if(courseManagerList.size() <= 1) {
            attributes.addFlashAttribute("type", "warning-top");
            attributes.addFlashAttribute("msg", "???????????????????????? ????????? ??????????????? ?????????.");
        } else {
            CourseManager courseManager = courseManagerService.getByUserId(userId);
            if(courseManager.getIsActive() == 1) {
                attributes.addFlashAttribute("type", "warning-top");
                attributes.addFlashAttribute("msg", "????????? ???????????? Active ??????????????? ????????? ??? ????????????.");
            } else{
                courseManagerService.delete(courseManager);
            }
        }

        return "redirect:/admin/registration/courseManager";
    }
}
