package com.cs322.services;

import com.cs322.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class InMemoryUserDetailsService implements UserDetailsService {

    private final Logger log = getLogger(this.getClass());
    private final static Map<String, User> inMemoryUsers = new HashMap<>();
    private static final String USER_NOT_FOUND = "USER_NOT_FOUND '%s'.";


    @PostConstruct
    private void getAllUsers() throws IOException {
        Gson gson = new Gson();

        JsonReader jsonReader = new JsonReader(
                new InputStreamReader(
                        new ClassPathResource("data/users.json").getInputStream()));
        JsonArray object = gson.fromJson(jsonReader, JsonArray.class);
//        gson.fromJson(jsonReader, new TypeToken<List<User>>() {
//        }.getType());
        for (JsonElement entry : object) {
            User user = gson.fromJson(entry, User.class);
            inMemoryUsers.put(user.getUsername(), user);
        }
        log.debug("getAllUser() users " + inMemoryUsers);
    }

    public User getUserByUsername(String username) {
        User user = inMemoryUsers.get(username);
        log.info("getUserByUsername() user " + user);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = Optional.ofNullable(inMemoryUsers.get(username));

        if (!user.isPresent()) {
            log.error("loadUserByUsername() UsernameNotFoundException " + username);
            throw new UsernameNotFoundException(String.format(USER_NOT_FOUND, username));
        }
        return user.get();
    }

    /**
     * if the query is exact username  we simply return that user
     * otherwise we go over every user and look for the query
     * in username and first,last names
     */
    public List<User> search(String query) {
        List<User> list = new ArrayList<>();
        if (StringUtils.isEmpty(query)) return list;
        if (inMemoryUsers.containsKey(query)) {
            list.add(inMemoryUsers.get(query));
        } else {
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