package com.cs322.models;

import lombok.*;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * JWT Toke Request POJO
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class JwtTokenRequest implements Serializable {

    private static final long serialVersionUID = -5616176897013108345L;

    private String username;
    private String password;

    public boolean isEmpty() {
        return StringUtils.isEmpty(username) || StringUtils.isEmpty(password);
    }
}
