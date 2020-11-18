package com.cs322.AuthService;

import com.cs322.services.InMemoryUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuthServiceApplicationTests {

	@Autowired
    InMemoryUserDetailsService userDetailsService;

	@Test
	void getUser() {
		userDetailsService.loadUserByUsername("president");
	}

	@Test
	void getUserWithError() {
		userDetailsService.loadUserByUsername("president");
	}

}
