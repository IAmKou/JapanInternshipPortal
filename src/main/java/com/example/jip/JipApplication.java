package com.example.jip;

import com.example.jip.services.AccountServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.Elements;

import java.util.Arrays;

@SpringBootApplication
public class JipApplication {
	public static void main(String[] args) {
		SpringApplication.run(JipApplication.class, args);
	}

}
