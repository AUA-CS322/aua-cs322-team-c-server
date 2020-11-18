package com.cs322.services;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import com.cs322.models.User;
import com.google.gson.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class InMemoryUserDetailsService implements UserDetailsService {

    private final static Map<String, User> inMemoryUsers = new HashMap<>();

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

    public User getUserByUsername(String username) {
        return inMemoryUsers.get(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = Optional.ofNullable(inMemoryUsers.get(username));

        if (!user.isPresent()) {
            throw new UsernameNotFoundException(String.format("USER_NOT_FOUND '%s'.", username));
        }

        return user.get();
    }

    /**
     * if there the query is exact username  we simply return that user
     * otherwise we go over every user and look for the query
     * in username and first,last names
     */
    public List<User> search(String query) {
        List<User> list = new ArrayList<>();
        if (inMemoryUsers.containsKey(query))
            list.add(inMemoryUsers.get(query));
        else {
            inMemoryUsers.forEach((key, user) -> {
                if (list.size() == 4) return;
                if (user.getUsername().contains(query)
                        || user.getFirstName().contains(query)
                        || user.getLastName().contains(query)) {
                    list.add(user);
                }
            });
        }
        return list;
    }
}