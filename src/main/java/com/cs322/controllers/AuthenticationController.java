package com.cs322.controllers;


import com.cs322.errors.ErrorMessages;
import com.cs322.exceptions.AuthenticationException;
import com.cs322.models.JsonError;
import com.cs322.models.JwtTokenRequest;
import com.cs322.models.JwtTokenResponse;
import com.cs322.services.InMemoryUserDetailsService;
import com.cs322.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final InMemoryUserDetailsService inMemoryUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, InMemoryUserDetailsService inMemoryUserDetailsService, JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.inMemoryUserDetailsService = inMemoryUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @RequestMapping(value = "${jwt.get.token.uri}", method = RequestMethod.POST)
    public ResponseEntity<Object> createAuthenticationToken(JwtTokenRequest authenticationRequest) {
        if (authenticationRequest.isEmpty())
            return new ResponseEntity<>(new JsonError(ErrorMessages.MISSING_DATA.name()), HttpStatus.BAD_REQUEST);
        try {
            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(new JsonError(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        final UserDetails userDetails = inMemoryUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtTokenResponse(token));
    }

    private void authenticate(String username, String password) {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new AuthenticationException(ErrorMessages.USER_DISABLED.name(), e);
        } catch (BadCredentialsException e) {
            throw new AuthenticationException(ErrorMessages.INVALID_CREDENTIALS.name(), e);
        }
    }
}