package com.cybr406.user.configuration;

import com.cybr406.user.ProfileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
public class UserConfiguration implements RepositoryRestConfigurer {

    @Autowired
    ProfileValidator profileValidator;

    @Override
    public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
        validatingListener.addValidator("beforeCreate", profileValidator);
        validatingListener.addValidator("beforeSave", profileValidator);
    }

    @Autowired
    DataSource dataSource;

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder passwordEncoder = passwordEncoder();

        // Create a UserBuilder
        User.UserBuilder users = User.builder();
        users.passwordEncoder(passwordEncoder::encode);

        auth
                .jdbcAuthentication()   // Authenticate users using via a jdbc (Java)
                .dataSource(dataSource) // Database connection info (user name, password, url, etc.)
                .withDefaultSchema()    // Use Spring Security's default table schema
                .withUser(users         // Add a pre-configured user to the database.
                        .username("admin")
                        .password("admin")
                        .roles("ADMIN"));
    }
}
