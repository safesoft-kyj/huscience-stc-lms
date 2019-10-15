package com.dtnsm.lms.auth;//package com.dtnsm.elearning.auth;

import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.Privilege;
import com.dtnsm.lms.domain.Role;
import com.dtnsm.lms.mybatis.dto.UserVO;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.repository.PrivilegeRepository;
import com.dtnsm.lms.repository.RoleRepository;
import com.dtnsm.lms.repository.UserRepository;
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
        Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        Privilege viewPrivilege = createPrivilegeIfNotFound("VIEW_PRIVILEGE");

        // 기본 롤을 만든다
        List<Privilege> adminPrivileges = Arrays.asList(readPrivilege, writePrivilege, viewPrivilege);
        createRoleIfNotFound("ROLE_ADMIN", "ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_MANAGER", "관리자", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", "직원", Arrays.asList(readPrivilege, viewPrivilege));
        createRoleIfNotFound("ROLE_OTHER", "외부사용자", Arrays.asList(viewPrivilege));

        // admin 롤을 가져온다
        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        Role userRole = roleRepository.findByName("ROLE_USER");

        // admin 계정이 없으면 만들고 Role을 부여한다
        Account user = createAccountAdminIfNotFound("admin", adminRole);

        // 개발자 계정이 없으면 만들고 Role을 부여한다
        Account devUser = createAccountDevIfNotFound("pub147", adminRole);


        // 그룹웨어 사용자 생성
        for(UserVO userVO : userMapperService.getUserAll()) {

            Account account = userRepository.findByUserId(userVO.getUserId());

            if (account == null) {
                account = new Account();
                account.setUserId(userVO.getUserId());
                account.setName(userVO.getKorName());
                account.setPassword(userVO.getPassword());
                account.setEmail(userVO.getEmail());
                account.setRoles(Arrays.asList(userRole));
                // 사용자 구분 (A:시스템사용자, U:내부직원, O:외부유저)
                account.setUserType("U");
                account.setEnabled(true);
                userRepository.save(account);
            }
        }

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

        Account user = userRepository.findByUserId("admin");

        if(user == null){
            user = new Account();
            user.setUserId("admin");
            user.setName("Admin");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setEmail("pub147@naver.com");
            user.setRoles(Arrays.asList(adminRole));
            // 사용자 구분 (A:admin, U:일반유저, O:외부유저)
            user.setUserType("A");
            user.setEnabled(true);
            userRepository.save(user);
        }

        return user;
    }

    // 개발자 계정
    private Account createAccountDevIfNotFound(String userId, Role adminRole) {

        Account user = userRepository.findByUserId(userId);

        if(user == null){
            user = new Account();
            user.setUserId(userId);
            user.setName("DevAdmin");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setEmail("pub147@safesoft.com");
            user.setRoles(Arrays.asList(adminRole));
            // 사용자 구분 (A:admin, U:일반유저, O:외부유저)
            user.setUserType("A");
            user.setEnabled(true);
            userRepository.save(user);
        }

        return user;
    }

}