package am.aua.cs322.orgchart;

import am.aua.cs322.orgchart.security.JwtInMemoryUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
