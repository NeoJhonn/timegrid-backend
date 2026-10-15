package br.com.jhonnyazevedo.timegrid_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class TimegridBackendApplication {

	static {
		// Define o fuso antes de inicializar Hibernate, validacoes e demais componentes.
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
	}

	public static void main(String[] args) {
		SpringApplication.run(TimegridBackendApplication.class, args);
	}

}
