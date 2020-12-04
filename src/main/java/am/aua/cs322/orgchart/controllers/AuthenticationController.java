package am.aua.cs322.orgchart.controllers;


import am.aua.cs322.orgchart.errors.ErrorMessages;
import am.aua.cs322.orgchart.exceptions.AuthenticationException;
import am.aua.cs322.orgchart.models.JsonError;
import am.aua.cs322.orgchart.models.JwtTokenRequest;
import am.aua.cs322.orgchart.models.JwtTokenResponse;
import am.aua.cs322.orgchart.utils.JwtTokenUtil;
import am.aua.cs322.orgchart.services.InMemoryUserDetailsService;
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

import java.util.Objects;

@RestController
@Log4j2
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
        if (authenticationRequest.isEmpty()) {
            log.info("createAuthenticationToken() object is empty");
            return new ResponseEntity<>(new JsonError(ErrorMessages.MISSING_DATA.name()), HttpStatus.BAD_REQUEST);
        }
        try {
            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(new JsonError(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        final UserDetails userDetails = inMemoryUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        log.info("createAuthenticationToken() userdetails " + userDetails.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        log.info("createAuthenticationToken() token " + token);
        JwtTokenResponse response = new JwtTokenResponse(token);
        log.info("createAuthenticationToken() response " + response);
        return ResponseEntity.ok(response);
    }

    private void authenticate(String username, String password) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            log.error("authenticate() DisabledException " + e.getMessage());
            throw new AuthenticationException(ErrorMessages.USER_DISABLED.name(), e);
        } catch (BadCredentialsException e) {
            log.error("authenticate() BadCredentialsException " + e.getMessage());
            throw new AuthenticationException(ErrorMessages.INVALID_CREDENTIALS.name(), e);
        }
    }
}
