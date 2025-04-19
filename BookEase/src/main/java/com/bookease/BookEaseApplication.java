package com.bookease;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class BookEaseApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
		System.out.println("Fuso horário forçado para: " + TimeZone.getDefault());

		SpringApplication.run(BookEaseApplication.class, args);
	}
}
