package am.aua.cs322.orgchart.controllers;

import am.aua.cs322.orgchart.models.User;
import am.aua.cs322.orgchart.services.InMemoryUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private InMemoryUserDetailsService inMemoryUserDetailsService;

    @GetMapping("/search")
    public List<User> search(String query){
        return inMemoryUserDetailsService.search(query);
    }
}