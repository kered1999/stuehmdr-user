package com.cybr406.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.Registration;
import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    JdbcUserDetailsManager userDetailsManager;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    User.UserBuilder userBuilder;

    @PostMapping("/signup")
    public ResponseEntity<Profile> signUp(@Valid @RequestBody Profile prof) {



        userDetailsManager.createUser(userBuilder
                .username(prof.getEmail())
                .password(prof.getPassword())
                .roles("BLOGGER")
                .build());

        Profile profile = new Profile();
        profile.setEmail(prof.getEmail());
        profile.setFirstName(prof.getFirstName());
        profile.setLastName(prof.getLastName());

        return new ResponseEntity<>(profileRepository.save(profile), HttpStatus.CREATED);
    }
}
