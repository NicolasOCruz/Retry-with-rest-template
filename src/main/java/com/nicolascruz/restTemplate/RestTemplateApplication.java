package com.nicolascruz.restTemplate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class RestTemplateApplication implements CommandLineRunner {

	private final RestTemplateCustomizer customizer;
	private static final String URL = "https://viacep.com.br/ws/01001000/json/";

	/*
	 * Construtor injection, a partir do Spring 4.3, caso seja um construtor único,
	 * ou seja, sem sobrecarga, não precisa da anotação @Autowired
	 */

	public RestTemplateApplication(RestTemplateCustomizer customizer) {
		this.customizer = customizer;
	}

	public static void main(String[] args) {
		SpringApplication.run(RestTemplateApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		RestTemplate restTemplate = new RestTemplateBuilder().additionalCustomizers(customizer).build();
		try {
			restTemplate.getForObject(URL, String.class);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
