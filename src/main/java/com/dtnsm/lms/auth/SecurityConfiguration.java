package com.dtnsm.lms.auth;

import com.dtnsm.lms.component.LoginSuccessHandler;
import com.dtnsm.lms.component.LogoutCustomSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserAuthenticationProvider authenticationProvider;

//    @Autowired
//    private UserService userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(
                        "/registration**",
                        "/js/**",
                        "/css/**",
                        "/img/**",
                        "/sano/**",
                        "/default/**",
                        "/plugins/**",
                        "/premium/**",
//                        "/admin/**",
//                        "/mypage/**",
//                        "/base/**",
//                        "/photoFile/**",
//                        "/approval/**",
//                        "/file_uploader/**",
//                        "/file_uploader_html5/**",
//                        "/content/**",
//                        "/fragments/**",
//                         "/home",
                         "/login",
                         "/sample/**",
                         "/lg/**",
                         "/webjars/**")
                        .permitAll()
                .anyRequest().authenticated();

         http.formLogin()
                .loginPage("/login")
                 .successHandler(successHandler())
                //.loginProcessingUrl("/authenticate")
//                .defaultSuccessUrl("/")
                .permitAll();

        http.sessionManagement()
                .invalidSessionUrl("/login?invalid")
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true)
                .expiredUrl("/login?duplicated");

         http.logout()
//                 .logoutSuccessHandler(logoutSuccessHandler())
//                 .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                 .logoutSuccessHandler(logoutSuccessHandler())
//                 .logoutSuccessUrl("/login")
                .permitAll();

         http.headers().frameOptions().disable();

         // ?????? ???????????????
        http.cors().and();

        http.csrf().disable();
    }

    // Login ?????? ??? ??????
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new LoginSuccessHandler();
    }

    // Logout ??????
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new LogoutCustomSuccessHandler();
    }

//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
//        auth.setUserDetailsService(userService);
//        auth.setPasswordEncoder(passwordEncoder());
//        return auth;
//    }
//
//        @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(authenticationProvider());
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

}