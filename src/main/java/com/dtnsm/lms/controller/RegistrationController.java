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
        pageInfo.setParentTitle("사용자관리");
    }

//    @ModelAttribute("user")
//    public UserRegistrationDto userRegistrationDto() {
//        return new UserRegistrationDto();
//    }

    @GetMapping("/account/add")
    public String showRegistrationUserForm(Model model) {

        pageInfo.setPageId("m-registration-add");
        pageInfo.setPageTitle("사용자");

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

        // 1.외부 사용자는 교육관리자를 상위결재권자로 지정한다.
//        if(account.getUserType().equals("O")){
//            if(!ObjectUtils.isEmpty(courseManagerService.getCourseManager())) {
//                account.setParentUserId(courseManagerService.getCourseManager().getAccount().getUserId());
//            } else {
//                //상위결재권자를 지정해 주세요. 문구 리다이렉트 필요
//            }
//        }



        // 초기 패스워드는 저장시 암호화 되며 Role은 사용자 타입에따라 자동 저장 됨
        Account account1 = userService.save(account);

//        // 2. 외부 사용자 빈이미지로 사인 등록
//        if(account.getUserType().equals("O")) {
//            Optional<Signature> optionalSignature = signatureRepository.findById(account1.getUserId());
//            if (!optionalSignature.isPresent()) {
//                Signature signature = new Signature();
//                signature.setId(account1.getUserId());
//                signature.setBase64signature("");
//                signatureRepository.save(signature);
//            }
//        }

        // 메일보내기(서비스 전까지는 메일 보내지 않음
        Mail mail = new Mail();

        StringBuilder sb = new StringBuilder();
        sb.append("ID : " + account1.getUserId() + "<br>");
        sb.append("PW : " + originPassword + "<br>");
        sb.append("사용자 등록이 완료되었습니다.");
//        sb.append("로그인후 패스워드를 변경하세요.<br>");
//        sb.append("<a href='http://lms.dtnsm.com'>LMS</a><br>");

        mail.setEmail(account1.getEmail());
        mail.setMessage(sb.toString());
        mail.setObject("[LMS/사용자] " + account1.getName() + "님 사용자 등록 완료");

        mailService.sendAccount(mail);

        return "redirect:/admin/registration/account";
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

        return "redirect:/admin/registration/account";
    }

    @GetMapping("/account")
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
        pageInfo.setPageTitle("사용자");
        List<Role> roleList = roleService.getList();

        // 나의 서명을 가지고 온다.
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

        // 1.외부 사용자 상위결재권자가 지정되어 있지 않은 경우 교육관리자로 지정한다.
        if (account.getParentUserId() == null || account.getParentUserId().isEmpty()) {
            if(courseManagerService.getCourseManager().getAccount().getUserId().isEmpty())
                return "redirect:/admin/registration/account";
            else
                account.setParentUserId(courseManagerService.getCourseManager().getAccount().getUserId());
        }
        // 저장한다.
        Account saveAccount = userRepository.save(account);

        // 2. 외부 사용자 사인이 등록되어 있지 않으면 사인 등록
        Optional<Signature> optionalSignature = signatureRepository.findById(saveAccount.getUserId());
        if(!optionalSignature.isPresent()) {
            Signature signature = new Signature();
            signature.setId(saveAccount.getUserId());
            signature.setBase64signature("");
            signatureRepository.save(signature);
        }

        //TODO 그룹웨어 관련 삭제
        // 내부직원인 경우 그룹웨어 정보를 업데이트 한다.
//        if (saveAccount.getUserType().equals("U")) {
//            userService.updateAccountByGroupwareInfo(saveAccount.getUserId());
//        }

        return "redirect:/admin/registration/account";
    }

    @GetMapping("/account/delete/{id}")
    public String deleteAccount(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {

        Account account = userService.getAccountByUserId(id);
        if(account.getName().contains("퇴사")){
            account.setEnabled(true);
            account.setName(account.getName().replaceAll("_퇴사",""));
            userRepository.save(account);
            redirectAttributes.addFlashAttribute("message","퇴사해제되었습니다.");
            return "redirect:/admin/registration/account";
        }
        //계정삭제 시 그 계정의 서명도 동시 삭제.
        Optional<Signature> signature = signatureRepository.findById(id);
        if (signature.isPresent()) {
            signatureRepository.deleteById(id);
        }
        account.setEnabled(false);
        account.setName(account.getName()+"_퇴사");
        userRepository.save(account);
        redirectAttributes.addFlashAttribute("message","퇴사처리되었습니다.");
        //계정삭제 시 그 계정의 직무 배정받은 정보 삭제.
        /*userJobDescriptionRepository.deleteByUsername(id);*/
        /*userService.deleteAccount(account);*/
        return "redirect:/admin/registration/account";
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
        사용자별 Role
     */

    @GetMapping("/accountRole/list")
    public String accountRoleList(Model model) {


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

        pageInfo.setParentTitle("교육과정기준정보");
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

        return "redirect:/admin/registration/courseManager";
    }


    @GetMapping("/courseManager/edit")
    public String courseManagerUpdate(@RequestParam("userId") String userId, Model model) {
        CourseManager courseManager = courseManagerService.getByUserId(userId);

        pageInfo.setParentTitle("교육과정기준정보");
        pageInfo.setPageTitle("교육과정관리자");
        model.addAttribute(pageInfo);
        model.addAttribute("accountList", userService.getAccountList());
        model.addAttribute("courseManager", courseManager);


        return "admin/registration/courseManager/edit";
    }

    @GetMapping("/courseManager/updateActive")
    public String updateActive(@RequestParam("userId") String userId) {

        // 모든 설문을 초기화 한다.
        for(CourseManager courseManager : courseManagerService.getList()) {
            courseManager.setIsActive(0);
            courseManagerService.save(courseManager);
        }

        // 요청된 설문을 기본 설문으로 변경한다.
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

        pageInfo.setParentTitle("교육과정기준정보");
        pageInfo.setPageTitle("교육과정관리자");

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
            attributes.addFlashAttribute("msg", "교육과정관리자는 반드시 설정되어야 합니다.");
        } else {
            CourseManager courseManager = courseManagerService.getByUserId(userId);
            if(courseManager.getIsActive() == 1) {
                attributes.addFlashAttribute("type", "warning-top");
                attributes.addFlashAttribute("msg", "선택한 관리자는 Active 상태임으로 삭제할 수 없습니다.");
            } else{
                courseManagerService.delete(courseManager);
            }
        }

        return "redirect:/admin/registration/courseManager";
    }
}
