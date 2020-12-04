package am.aua.cs322.orgchart.models;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Relationship implements Serializable {

    private List<User> parent = new ArrayList<>();
    private List<User> children = new ArrayList<>();

    public void addParent(User user){
        if(user != null){
            parent.add(user);
        }
    }

    public void addChild(User user){
        if(user != null){
            children.add(user);
        }
    }
}
