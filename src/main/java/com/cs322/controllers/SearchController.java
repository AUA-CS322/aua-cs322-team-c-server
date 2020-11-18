package com.cs322.controllers;

import com.cs322.models.User;
import com.cs322.services.InMemoryUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private InMemoryUserDetailsService inMemoryDatabase;

    @GetMapping("/search")
    public List<User> search(String query){
        return inMemoryDatabase.search(query);
    }
}