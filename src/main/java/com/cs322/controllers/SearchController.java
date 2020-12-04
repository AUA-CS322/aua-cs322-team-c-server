package com.cs322.controllers;

import com.cs322.models.User;
import com.cs322.services.LuceneSearchingService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
public class SearchController {
    private final LuceneSearchingService luceneSearchingService;

    @Autowired
    public SearchController(LuceneSearchingService luceneSearchingService) {
        this.luceneSearchingService = luceneSearchingService;
    }

    @GetMapping("/search")
    public List<User> search(String query) {
        log.info("search() query " + query);
        List<User> result = luceneSearchingService.searchUsers(query);
        log.info("search() result " + result);
        return result;
    }
}