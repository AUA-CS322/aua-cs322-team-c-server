package com.cs322.models;

import lombok.*;

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

}
