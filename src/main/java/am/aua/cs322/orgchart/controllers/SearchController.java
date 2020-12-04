package am.aua.cs322.orgchart.controllers;

import am.aua.cs322.orgchart.models.User;
import am.aua.cs322.orgchart.services.InMemoryUserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    private final InMemoryUserDetailsService inMemoryUserDetailsService;

    public SearchController(InMemoryUserDetailsService inMemoryUserDetailsService) {
        this.inMemoryUserDetailsService = inMemoryUserDetailsService;
    }

    @GetMapping("/search")
    public List<User> search(String query){
        return inMemoryUserDetailsService.search(query);
    }
}