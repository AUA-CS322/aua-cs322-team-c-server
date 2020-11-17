package com.cs322.AuthService.Security;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import com.cs322.AuthService.Model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class JwtInMemoryUserDetailsService implements UserDetailsService {

    static Map<String, User> inMemoryUsers = new HashMap<>();

    @PostConstruct
    private void getAllUsers() throws FileNotFoundException {
        JsonElement root = new JsonParser().parse(new FileReader("src/main/resources/data/users.json"));
        JsonArray object = root.getAsJsonArray();

        Gson gson = new Gson();
        for (JsonElement entry : object) {
            User user = gson.fromJson(entry, User.class);
            inMemoryUsers.put(user.getUsername(), user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = Optional.ofNullable(inMemoryUsers.get(username));

        if (!user.isPresent()) {
            throw new UsernameNotFoundException(String.format("USER_NOT_FOUND '%s'.", username));
        }

        return user.get();
    }
}

