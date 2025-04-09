package com.bank.mqmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MqManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(MqManagementApplication.class, args);
	}

}
