package am.aua.cs322.orgchart.controllers;

import am.aua.cs322.orgchart.models.Relationship;
import am.aua.cs322.orgchart.models.User;
import am.aua.cs322.orgchart.utils.JwtTokenUtil;
import am.aua.cs322.orgchart.services.InMemoryUserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserController {

    private final JwtTokenUtil jwtTokenUtil;

    private final InMemoryUserDetailsService inMemoryDatabase;

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
        return inMemoryDatabase.getUserByUsername(username);
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
