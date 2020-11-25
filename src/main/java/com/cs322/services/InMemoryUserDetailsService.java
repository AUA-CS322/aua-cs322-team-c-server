

package com.cs322.services;

import com.cs322.models.Relationship;
import com.cs322.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

@Service
public class InMemoryUserDetailsService implements UserDetailsService {

    private final static Map<String, User> inMemoryUsers = new HashMap<>();

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class OrgTree {
        private UUID id;
        private UUID parent;
    }

    @PostConstruct
    private void getAllUsers() throws FileNotFoundException {
        JsonElement users = new JsonParser().parse(new FileReader("src/main/resources/data/users.json"));
        JsonArray usersArray = users.getAsJsonArray();

        Gson gson = new Gson();
        for (JsonElement entry : usersArray) {
            User user = gson.fromJson(entry, User.class);
            inMemoryUsers.put(user.getUsername(), user);
        }

        List<OrgTree> orgTrees = new ArrayList<>();
        JsonElement orgTree = new JsonParser().parse(new FileReader("src/main/resources/data/org-tree.json"));
        JsonArray orgTreeArray = orgTree.getAsJsonArray();
        for (JsonElement entry : orgTreeArray) {
            OrgTree tree = gson.fromJson(entry, OrgTree.class);
            orgTrees.add(tree);
        }

        for (OrgTree tr : orgTrees) {
            for (Map.Entry<String, User> userEntry : inMemoryUsers.entrySet()) {
                User user = userEntry.getValue();
                if (user.getId().compareTo(tr.getId()) == 0) {
                    user.setParentId(tr.parent);
                }
            }
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

    public Relationship getUserOrgChart(String username) {
        Relationship relationship = new Relationship();
        User user = getUserByUsername(username);

        for (User u : inMemoryUsers.values()) {
            if (user.getParentId() != null && user.getParentId().compareTo(u.getId()) == 0) {
                relationship.addParent(u);
            }
            if (u.getParentId() != null && user.getId().compareTo(u.getParentId()) == 0) {
                relationship.addChild(u);
            }
        }

        return relationship;
    }
}
