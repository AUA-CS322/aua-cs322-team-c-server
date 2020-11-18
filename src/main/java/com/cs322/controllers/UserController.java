package com.cs322.controllers;

import com.cs322.models.User;
import com.cs322.services.InMemoryUserDetailsService;
import com.cs322.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private InMemoryUserDetailsService inMemoryDatabase;

    @GetMapping("/user")
    public User getUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization")
                .replace("Bearer", "")
                .trim();
        String username = jwtTokenUtil.getUsernameFromToken(token);
        return inMemoryDatabase.getUserByUsername(username);
    }
}
