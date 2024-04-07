package com.lkksoftdev.loanservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class LoanServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoanServiceApplication.class, args);
	}

}
