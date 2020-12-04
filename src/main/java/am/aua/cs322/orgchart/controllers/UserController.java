package am.aua.cs322.orgchart.controllers;

import am.aua.cs322.orgchart.models.Relationship;
import am.aua.cs322.orgchart.models.User;
import am.aua.cs322.orgchart.services.InMemoryUserDetailsService;
import am.aua.cs322.orgchart.services.LuceneSearchingService;
import am.aua.cs322.orgchart.utils.JwtTokenUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Log4j2
public class UserController {

    private final JwtTokenUtil jwtTokenUtil;

    private final InMemoryUserDetailsService inMemoryDatabase;

    private final LuceneSearchingService luceneSearchingService;


    @Autowired
    public UserController(JwtTokenUtil jwtTokenUtil, InMemoryUserDetailsService inMemoryDatabase,
                          LuceneSearchingService luceneSearchingService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.inMemoryDatabase = inMemoryDatabase;
        this.luceneSearchingService = luceneSearchingService;
    }

    @GetMapping("/users/user")
    public User getMe(HttpServletRequest request) {
        String token = request.getHeader("Authorization")
                .replace("Bearer", "")
                .trim();
        String username = jwtTokenUtil.getUsernameFromToken(token);
        log.info("getMe() " + username);
        User userByUsername = luceneSearchingService.getUser(username);
        log.debug("getMe() result " + userByUsername);
        return userByUsername;
    }

    @GetMapping("/users/{user}")
    public User getUser(@PathVariable(name = "user") String username) {
        return luceneSearchingService.getUser(username);
    }

    @GetMapping("/org-chart/{user}")
    public Relationship getUserOrgChart(@PathVariable(name = "user") String username) {
        return inMemoryDatabase.getUserOrgChart(username);
    }
}
