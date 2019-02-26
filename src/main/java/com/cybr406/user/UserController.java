package com.cybr406.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.Registration;
import javax.validation.Valid;

@RestController
public class UserController {
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(new SignUpValidator());
    }

    @Autowired
    JdbcUserDetailsManager userDetailsManager;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    User.UserBuilder userBuilder;

    @PostMapping("/signup")
    public ResponseEntity<Profile> signUp(@Valid @RequestBody SignUp signUp) {
        userDetailsManager.createUser(userBuilder
                .username(signUp.getEmail())
                .password(signUp.getPassword())
                .roles("BLOGGER")
                .build());

        Profile profile = new Profile();
        profile.setFirstName(signUp.getFirstName());
        profile.setLastName(signUp.getLastName());
        profile.setEmail(signUp.getEmail());

        return new ResponseEntity<>(profileRepository.save(profile), HttpStatus.CREATED);

    }
}
