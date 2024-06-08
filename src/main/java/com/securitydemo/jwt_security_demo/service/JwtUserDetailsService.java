package com.securitydemo.jwt_security_demo.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.securitydemo.jwt_security_demo.controller.DemoController;
import com.securitydemo.jwt_security_demo.entity.User;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService{

    @Autowired
    com.securitydemo.jwt_security_demo.repository.UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(JwtUserDetailsService.class);


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("user load by user "+username);
        User user = userRepository.loadUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return user;
    }
}
