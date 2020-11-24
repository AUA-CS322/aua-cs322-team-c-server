package com.cs322.controllers;

import com.cs322.models.User;
import com.cs322.services.InMemoryUserDetailsService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class SearchController {
    private final Logger log = getLogger(this.getClass());


    private final InMemoryUserDetailsService inMemoryUserDetailsService;

    @Autowired
    public SearchController(InMemoryUserDetailsService inMemoryUserDetailsService) {
        this.inMemoryUserDetailsService = inMemoryUserDetailsService;
    }

    @GetMapping("/search")
    public List<User> search(String query) {
        log.info("search() query " + query);
        List<User> result = inMemoryUserDetailsService.search(query);
        log.info("search() result " + result);
        return result;
    }
}