//package com.dtnsm.lms.auth;//package com.dtnsm.elearning.auth;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import javax.transaction.Transactional;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.List;
//
//@Component
//public class InitialDataLoader implements
//        ApplicationListener<ContextRefreshedEvent> {
//
//    boolean alreadySetup = false;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private PrivilegeRepository privilegeRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Override
//    @Transactional
//    public void onApplicationEvent(ContextRefreshedEvent event) {
//
//        if (alreadySetup)
//            return;
//
//        // 기본 권한을 만든다
//        Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
//        Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
//        Privilege viewPrivilege = createPrivilegeIfNotFound("VIEW_PRIVILEGE");
//
//        // 기본 롤을 만든다
//        List<Privilege> adminPrivileges = Arrays.asList(readPrivilege, writePrivilege, viewPrivilege);
//        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
//        createRoleIfNotFound("ROLE_USER", Arrays.asList(readPrivilege, viewPrivilege));
//
//        // admin 롤을 가져온다
//        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
//
//        // admin 계정이 없으면 만들고 Role을 부여한다
//        Account user = createAccountAdminIfNotFound("admin", adminRole);
//
//        alreadySetup = true;
//    }
//
//
//    private Privilege createPrivilegeIfNotFound(String name) {
//
//        Privilege privilege = privilegeRepository.findByName(name);
//        if (privilege == null) {
//            privilege = new Privilege(name);
//            privilegeRepository.save(privilege);
//        }
//        return privilege;
//    }
//
//    private Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {
//
//        Role role = roleRepository.findByName(name);
//        if (role == null) {
//            role = new Role(name);
//            role.setPrivileges(privileges);
//            roleRepository.save(role);
//        }
//        return role;
//    }
//
//    private Account createAccountAdminIfNotFound(String userId, Role adminRole) {
//
//        Account user = userRepository.findByUserId("admin");
//
//        if(user == null){
//            user = new Account();
//            user.setUserId("admin");
//            user.setName("Admin");
//            user.setPassword(passwordEncoder.encode("admin"));
//            user.setEmail("pub147@naver.com");
//            user.setRoles(Arrays.asList(adminRole));
//            user.setEnabled(true);
//            userRepository.save(user);
//        }
//
//        return user;
//    }
//
//}