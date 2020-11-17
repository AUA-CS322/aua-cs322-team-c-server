package com.cs322.AuthService.Model;

import java.io.Serializable;

/**
 *  JWT Toke Response POJO
 */

public class JwtTokenResponse implements Serializable {

    private static final long serialVersionUID = 8317676219297719109L;

    private final String token;

    public JwtTokenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}
