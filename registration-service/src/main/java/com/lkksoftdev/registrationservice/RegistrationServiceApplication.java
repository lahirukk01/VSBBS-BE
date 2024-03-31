package com.lkksoftdev.registrationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RegistrationServiceApplication {
	private static String generatedPassword() {
		var passwordEncoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
		String encryptedPassword = passwordEncoder.encode("password");
		System.out.println("Password: " + encryptedPassword);
		return encryptedPassword;
	}

	public static void main(String[] args) {
		SpringApplication.run(RegistrationServiceApplication.class, args);
	}

}
