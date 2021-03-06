package com.dtnsm.lms.auth;//package com.dtnsm.elearning.auth;

import com.dtnsm.lms.domain.*;
import com.dtnsm.lms.mybatis.dto.UserVO;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class InitialDataLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapperService userMapperService;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;

        // 기본 권한을 만든다
//        Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
//        Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
//        Privilege viewPrivilege = createPrivilegeIfNotFound("VIEW_PRIVILEGE");
//
//        // 기본 롤을 만든다
//        List<Privilege> adminPrivileges = Arrays.asList(readPrivilege, writePrivilege, viewPrivilege);
//        createRoleIfNotFound("ROLE_ADMIN", "ADMIN", adminPrivileges);
//        createRoleIfNotFound("ROLE_MANAGER", "관리자", adminPrivileges);
//        createRoleIfNotFound("ROLE_USER", "직원", Arrays.asList(readPrivilege, viewPrivilege));
//        createRoleIfNotFound("ROLE_OTHER", "외부사용자", Arrays.asList(viewPrivilege));
//
//        // admin 롤을 가져온다
//        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
//        Role userRole = roleRepository.findByName("ROLE_USER");
//
//        // admin 계정이 없으면 만들고 Role을 부여한다
//        Account user = createAccountAdminIfNotFound("admin", adminRole);
//
//        // 개발자 계정이 없으면 만들고 Role을 부여한다
//        Account devUser = createAccountDevIfNotFound("pub147", adminRole);


//        // 그룹웨어 사용자 생성
//        for(UserVO userVO : userMapperService.getUserAll()) {
//
//            Account account = userRepository.findByUserId(userVO.getUserId());
//
//            // 새로운 그룹웨어 유저가 추가되었을때 Account 계정을 추가한다.(적용전에는 전체 업데이트)
//            if (account == null) {
//                account = new Account();
//                account.setUserId(userVO.getUserId());
//                account.setName(userVO.getKorName());
//                account.setEngName(userVO.getEngName());
//                account.setComNum(userVO.getComNum());
//                account.setPassword(userVO.getPassword());
//                account.setEmail(userVO.getEmail());
//                account.setComJob(userVO.getComJob());
//                account.setComPosition(userVO.getComPosition());
//                account.setOrgDepart(userVO.getOrgDepart());
//                account.setIndate(userVO.getIndate());
//                account.setRoles(Arrays.asList(userRole));
//                // 사용자 구분 (U:내부직원, O:외부유저)
//                account.setUserType("U");
//                account.setEnabled(true);
//                userRepository.save(account);
//            }
//
//        }

//        addIndication(1, "Ulcerative Colitis");
//        addIndication(2, "Breast Cancer");
//        addIndication(3, "Type 2 Diabetes Mellitus");
        


//        for(int i = 0; i < phaseList.size(); i ++) {
//            addPhase(i + 1, phaseList.get(i));
//        }
        alreadySetup = true;
    }


    private Privilege createPrivilegeIfNotFound(String name) {

        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

//    private void addIndication(Integer id, String indication) {
//        CVIndication cvIndication = new CVIndication();
//        cvIndication.setId(id);
//        cvIndication.setIndication(indication);
//
//        indicationRepository.save(cvIndication);
//    }
//
//    private void addPhase(Integer id, String phase) {
//        CVPhase cvPhase = new CVPhase();
//        cvPhase.setId(id);
//        cvPhase.setPhase(phase);
//
//        phaseRepository.save(cvPhase);
//    }

    private Role createRoleIfNotFound(String name, String memo, Collection<Privilege> privileges) {

        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setMemo(memo);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }

    // 어드민 계정
    private Account createAccountAdminIfNotFound(String userId, Role adminRole) {

        Account account = userRepository.findByUserId("admin");

        if(account == null){
            account = new Account();
            account.setUserId("admin");
            account.setName("Admin");
            account.setEngName("Admin");
            account.setComNum("S99999999");
            account.setPassword(passwordEncoder.encode("admin"));
            account.setEmail("ks.hwang@safesoft.co.kr");
            account.setComJob("ComJob");
            account.setComPosition("ComPosition");
            account.setOrgDepart("ComDeprat");
            account.setOrgTeam("ComTeam");
            account.setIndate("2019-12-01");
            account.setRoles(Arrays.asList(adminRole));
            // 사용자 구분 (U:내부직원, O:외부유저)
            account.setUserType("S");
            account.setEnabled(true);

            userRepository.save(account);
        }

        return account;
    }

    // 개발자 계정
    private Account createAccountDevIfNotFound(String userId, Role adminRole) {

        Account account = userRepository.findByUserId(userId);

        if(account == null){
            account = new Account();
            account.setName("황강석");
            account.setEngName("kang Seok Hwange");
            account.setComNum("S11111111");
            account.setPassword(passwordEncoder.encode("admin"));
            account.setEmail("ks.hwang@safesoft.co.kr");
            account.setComJob("ComJob");
            account.setComPosition("ComPosition");
            account.setOrgDepart("ComDeprat");
            account.setOrgTeam("ComTeam");
            account.setIndate("2019-12-01");
            account.setRoles(Arrays.asList(adminRole));
            // 사용자 구분 (U:내부직원, O:외부유저)
            account.setUserType("S");
            account.setEnabled(true);
            userRepository.save(account);
        }

        return account;
    }

}