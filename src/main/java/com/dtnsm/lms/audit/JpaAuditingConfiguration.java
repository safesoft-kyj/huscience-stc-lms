package com.dtnsm.lms.audit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@Configuration
@EnableJpaAuditing(auditorAwareRef = "commonAuditorAware")

public class JpaAuditingConfiguration {


    @Bean
    public AuditorAware<String> commonAuditorAware() {

        /*
          if you are using spring security, you can get the currently logged username with following code segment.
          SecurityContextHolder.getContext().getAuthentication().getName()
         */

        return new CommonAuditorAware();
    }
}