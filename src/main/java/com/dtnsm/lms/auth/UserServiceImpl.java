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

        if(account.getRoles() == null || account.getRoles().size() == 0) {
            Role role;

            if (account.getUserType().equals("U")) {
                role = roleRepository.findByName("ROLE_USER");
            } else {
                role = roleRepository.findByName("ROLE_OTHER");
            }
            account.setRoles(Arrays.asList(role));
        }

        // 사용자 구분 (U:일반유저, O:외부유저)
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
                newUser.setOrgTeam(userVO.getOrgTeam());
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

    // 그룹웨어 사용자 정보로 Account 계정의 정보를 생성하거나 업데이트 한다.
    public void updateAccountByGroupwareInfo(String userId) {

        // 기존에 Account가 존재하는 확인
        Account account = userRepository.findByUserId(userId);

        // 그룹웨어 사용자 정보를 가지고 온다.
        UserVO userVO = userMapperService.getUserById(userId);

        // Account 가 없으면
        if (account == null) {

            // 그룹웨어 사용자 정보가 있으면 Account를 생성한다.
            if (userVO != null) {
                // 내부직원 기본 사용자롤 설정
                Role userRole = roleRepository.findByName("ROLE_USER");

                account = new Account();
                account.setUserId(userVO.getUserId());
                account.setName(userVO.getKorName());
                account.setEngName(userVO.getEngName());
                account.setComNum(userVO.getComNum());
                account.setPassword(userVO.getPassword());
                account.setEmail(userVO.getEmail());
                account.setComJob(userVO.getComJob());
                account.setComPosition(userVO.getComPosition());
                account.setOrgDepart(userVO.getOrgDepart());
                account.setOrgTeam(userVO.getOrgTeam() == null ? "" : userVO.getOrgTeam());
                account.setIndate(userVO.getIndate());
                account.setRoles(Arrays.asList(userRole));
                // 사용자 구분 (U:내부직원, O:외부유저)
                account.setUserType("U");
                account.setEnabled(true);
                userRepository.save(account);
            }
        } else {
            if (userVO != null) {
                account.setName(userVO.getKorName());
                account.setEngName(userVO.getEngName());
                account.setComNum(userVO.getComNum());
                account.setPassword(userVO.getPassword());
                account.setEmail(userVO.getEmail());
                account.setComJob(userVO.getComJob());
                account.setComPosition(userVO.getComPosition());
                account.setOrgDepart(userVO.getOrgDepart());
                account.setOrgTeam(userVO.getOrgTeam() == null ? "" : userVO.getOrgTeam());
                account.setIndate(userVO.getIndate());
//                account.setRoles(Arrays.asList(userRole));
                // 사용자 구분 (U:내부직원, O:외부유저)
//                account.setUserType("U");
//                account.setEnabled(true);
                userRepository.save(account);
            }
        }
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