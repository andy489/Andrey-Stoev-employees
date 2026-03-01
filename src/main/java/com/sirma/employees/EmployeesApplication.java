package com.sirma.employees;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
@EnableScheduling
public class EmployeesApplication {

	private static final String SELF_URL = "https://your-app-name.onrender.com/health";
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private final RestTemplate restTemplate = new RestTemplate();

	public static void main(String[] args) {
		SpringApplication.run(EmployeesApplication.class, args);
	}

	@Scheduled(fixedRate = 840000) // 14 minutes in milliseconds
	public void keepAlive() {
		String timestamp = LocalDateTime.now().format(formatter);

		try {
			String response = restTemplate.getForObject(SELF_URL, String.class);
			System.out.println("[" + timestamp + "] ✅ Health check passed: " + response);

		} catch (HttpClientErrorException e) {
			System.err.println("[" + timestamp + "] ❌ Client error (" +
					e.getStatusCode() + "): " + e.getMessage());
		} catch (HttpServerErrorException e) {
			System.err.println("[" + timestamp + "] ❌ Server error (" +
					e.getStatusCode() + "): " + e.getMessage());
		} catch (ResourceAccessException e) {
			System.err.println("[" + timestamp + "] ❌ Connection error: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("[" + timestamp + "] ❌ Unexpected error: " + e.getMessage());
		}
	}
}
