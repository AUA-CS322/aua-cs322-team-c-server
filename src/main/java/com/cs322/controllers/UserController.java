package com.cs322.controllers;

import com.cs322.models.Relationship;
import com.cs322.models.User;
import com.cs322.services.InMemoryUserDetailsService;
import com.cs322.utils.JwtTokenUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class UserController {
    private final Logger log = getLogger(this.getClass());

    private final JwtTokenUtil jwtTokenUtil;

    private final InMemoryUserDetailsService inMemoryDatabase;

    @Autowired
    public UserController(JwtTokenUtil jwtTokenUtil, InMemoryUserDetailsService inMemoryDatabase) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.inMemoryDatabase = inMemoryDatabase;
    }

    @GetMapping("/users/user")
    public User getMe(HttpServletRequest request) {
        String token = request.getHeader("Authorization")
                .replace("Bearer", "")
                .trim();
        String username = jwtTokenUtil.getUsernameFromToken(token);
        log.info("getMe() " + username);
        User userByUsername = inMemoryDatabase.getUserByUsername(username);
        log.debug("getMe() result " + userByUsername);
        return userByUsername;
    }

    @GetMapping("/users/{user}")
    public User getUser(@PathVariable(name = "user") String username) {
        return inMemoryDatabase.getUserByUsername(username);
    }

    @GetMapping("/org-chart/{user}")
    public Relationship getUserOrgChart(@PathVariable(name = "user") String username) {
        return inMemoryDatabase.getUserOrgChart(username);
    }
}
