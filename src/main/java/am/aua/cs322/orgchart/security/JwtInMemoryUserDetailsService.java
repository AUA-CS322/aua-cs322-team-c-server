package am.aua.cs322.orgchart.security;

import am.aua.cs322.orgchart.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtInMemoryUserDetailsService implements UserDetailsService {

    static Map<String, User> inMemoryUsers = new HashMap<>();

    @PostConstruct
    private void getAllUsers() throws FileNotFoundException {
        JsonElement root = new JsonParser().parse(getReaderFromFileName("/data/users.json"));
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

    /**
     * Returns BufferedReader for give file. Other methods fail when the application is packages as Jar
     *
     * @param path relative path of the file to the classpath
     * @return BufferedReader for current file
     */
    private BufferedReader getReaderFromFileName(String path) {
        InputStream in = getClass().getResourceAsStream(path);
        return new BufferedReader(new InputStreamReader(in));
    }
}

