package com.dtnsm.lms.auth;

import com.dtnsm.lms.domain.Account;
import com.dtnsm.lms.domain.ElMinor;
import com.dtnsm.lms.domain.Role;
import com.dtnsm.lms.mybatis.dto.UserVO;
import com.dtnsm.lms.mybatis.service.UserMapperService;
import com.dtnsm.lms.repository.RoleRepository;
import com.dtnsm.lms.repository.UserRepository;
import com.dtnsm.lms.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CodeService codeService;

    @Autowired
    UserMapperService userMapperService;

    @Autowired
    private PasswordEncoding passwordEncoder;

    private static String TypeMajorCd = "BA02";

    public Account findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    public Account save(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));

        if(account.getRoles().size() == 0) {
            Role role;

            if (account.getUserType().equals("A")) {
                role = roleRepository.findByName("ROLE_ADMIN");
            } else if (account.getUserType().equals("U")) {
                role = roleRepository.findByName("ROLE_USER");
            } else {
                role = roleRepository.findByName("ROLE_OTHER");
            }
            account.setRoles(Arrays.asList(role));
        }

        // 사용자 구분 (A:admin, U:일반유저, O:외부유저)
        //account.setUserType("O");
        return userRepository.save(account);
    }

//    public Account save(UserRegistrationDto registration) {
//        Account user = new Account();
//        user.setUserId(registration.getUserId());
//        user.setName(registration.getName());
//        user.setEmail(registration.getEmail());
//        user.setPassword(passwordEncoder.encode(registration.getPassword()));
//
//        Role role = roleRepository.findByName("ROLE_OTHER");
//        user.setRoles(Arrays.asList(role));
//        // 사용자 구분 (A:admin, U:일반유저, O:외부유저)
//        user.setUserType("O");
//        return userRepository.save(user);
//    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        // LMS 사용자 정보를 가지고 온다.
        Account user = userRepository.findByUserId(userId);

        if (user == null) {
            // 그룹웨어 사용자 정보를 가지고 온다.
            UserVO userVO = userMapperService.getUserById(userId);

            if(userVO != null) {

                Role role = roleRepository.findByName("ROLE_USER");

                Account newUser = new Account();
                newUser.setUserId(userId);
                newUser.setName(userVO.getKorName());
                newUser.setEngName(userVO.getEngName());
                newUser.setPassword(userVO.getPassword());
                newUser.setEmail(userVO.getEmail());
                newUser.setRoles(Arrays.asList(role));
                // 사용자 구분 (A:admin, U:일반유저, O:외부유저)
                newUser.setUserType("U");
                newUser.setEnabled(true);
                newUser.setOrgDepart(userVO.getOrgDepart());
                newUser.setComPosition(userVO.getComPosition());
                newUser.setIndate(userVO.getIndate());
                user = userRepository.save(newUser);
            }
        }

        CustomUserDetails userDetails = null;

        if(user != null) {
            userDetails = new CustomUserDetails();
            userDetails.setUser(user);
        } else {
            throw new UsernameNotFoundException("User not exist with name : " + userId);
        }


        return userDetails;

//        return new org.springframework.security.core.userdetails.User(user.getUserId(),
//                user.getPassword(),
//                mapRolesToAuthorities(user.getRoles()));
    }

    public UserDetails loadUserByUsername(String userId, String password) throws UsernameNotFoundException {

        String password1 = passwordEncoder.encode(password);

        Account user = userRepository.findByUserId(userId);

        CustomUserDetails userDetails = null;


        if(user != null) {
            boolean isMatch = passwordEncoder.matches(password, user.getPassword());

            if(isMatch) {
                userDetails = new CustomUserDetails();
                userDetails.setUser(user);
            } else {
                throw new UsernameNotFoundException("User not exist with name : " + userId);
            }
        } else {
            throw new UsernameNotFoundException("User not exist with name : " + userId);
        }

        return userDetails;

//        return new org.springframework.security.core.userdetails.User(user.getUserId(),
//                user.getPassword(),
//                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection < ? extends GrantedAuthority > mapRolesToAuthorities(Collection < Role > roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }


    public Optional<List<Account>> findByParentUserId(String userId) {
        return userRepository.findByParentUserId(userId);
    }

    /*
        Account
     */

    public List<Account> getAccountList() {
        return userRepository.findAll();
    }

    public Account getAccountByUserId(String userId) {

        return userRepository.findByUserId(userId);
    }

    public Account getAccountByName(String name) {

        return userRepository.findByName(name);
    }

    public Account saveAccount(Account account){

        return userRepository.save(account);
    }

    public void deleteAccount(Account account) {
        userRepository.delete(account);

    }




    /*
        Role
     */

    public List<Role> getRoleList() {

        return roleRepository.findAll();
    }

    public Role getRoleById(Long id) {

        return roleRepository.findById(id).get();
    }

    public Role saveAccount(Role role){

        return roleRepository.save(role);
    }

    public void deleteRole(Role role) {
        roleRepository.delete(role);

    }

    /*
        Account Role

     */
    public Account accountRoleAdd(Account account, Role role) {

        Collection<Role> roles =  account.getRoles();
        roles.add(role);
        account.setRoles(roles);

        return userRepository.save(account);
    }

    public Account accountRoleDelete(Account account, Role role) {

        Collection<Role> roles =  account.getRoles();
        roles.remove(role);
        account.setRoles(roles);
        return userRepository.save(account);
    }

    /*

        코드
     */

    public List<ElMinor> getTypeList() {
        return codeService.getMinorList(TypeMajorCd);
    }
}