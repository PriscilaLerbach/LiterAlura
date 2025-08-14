package br.com.alura.literalura.LiterAlura;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiterAluraApplication {

	public static void main(String[] args) {
		//new ServicoApiGutendex(new ObjectMapper()).buscarLivroPorTitulo("Don Quixote").ifPresent(System.out::println);
		SpringApplication.run(LiterAluraApplication.class, args);
	}
}
