package am.aua.cs322.orgchart.services;

import am.aua.cs322.orgchart.models.Relationship;
import am.aua.cs322.orgchart.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class InMemoryUserDetailsService implements UserDetailsService {

    private final static Map<String, User> inMemoryUsers = new HashMap<>();

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class OrgTree{
        private UUID id;
        private UUID parent;
    }

    @PostConstruct
    private void getAllUsers() throws IOException {
        JsonReader jsonReader = new JsonReader(
                new InputStreamReader(
                        new ClassPathResource("data/users.json").getInputStream()));

        Gson gson = new Gson();
        JsonArray usersArray = gson.fromJson(jsonReader, JsonArray.class);

        for (JsonElement entry : usersArray) {
            User user = gson.fromJson(entry, User.class);
            inMemoryUsers.put(user.getUsername(), user);
        }

        List<OrgTree> orgTrees = new ArrayList<>();

        jsonReader = new JsonReader(
                new InputStreamReader(
                        new ClassPathResource("data/org-tree.json").getInputStream()));
        JsonArray orgTree = gson.fromJson(jsonReader, JsonArray.class);

        JsonArray orgTreeArray = orgTree.getAsJsonArray();
        for (JsonElement entry : orgTreeArray) {
            OrgTree tree = gson.fromJson(entry, OrgTree.class);
            orgTrees.add(tree);
        }

        for(OrgTree tr: orgTrees){
            for(Map.Entry<String, User> userEntry: inMemoryUsers.entrySet()){
                User user = userEntry.getValue();
                if(user.getId().compareTo(tr.getId())==0){
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

    public Relationship getUserOrgChart(String username){
        Relationship relationship = new Relationship();
        User user = getUserByUsername(username);

        for(User u: inMemoryUsers.values()){
            if(user.getParentId() != null && user.getParentId().compareTo(u.getId())==0){
                relationship.addParent(u);
            }
            if(u.getParentId() != null && user.getId().compareTo(u.getParentId())==0){
                relationship.addChild(u);
            }
        }

        return relationship;
    }
}
