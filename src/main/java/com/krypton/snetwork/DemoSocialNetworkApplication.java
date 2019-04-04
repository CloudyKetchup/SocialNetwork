package com.krypton.snetwork;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class DemoSocialNetworkApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoSocialNetworkApplication.class, args);
	}
}
