package com.cs322.AuthService;

import com.cs322.AuthService.Security.JwtInMemoryUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

@SpringBootTest
class AuthServiceApplicationTests {

	@Autowired
	JwtInMemoryUserDetailsService userDetailsService;

	@Test
	void getUser() {
		userDetailsService.loadUserByUsername("president");
	}

	@Test
	void getUserWithError() {
		userDetailsService.loadUserByUsername("president");
	}

}
