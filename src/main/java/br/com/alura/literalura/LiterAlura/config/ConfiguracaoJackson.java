package br.com.alura.literalura.LiterAlura.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

/**
 * Configuracao do Jackson para processamento de JSON
 */
@Configuration
public class ConfiguracaoJackson {

    /**
     * Cria e configura um ObjectMapper para processamento de JSON
     * @return ObjectMapper configurado
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}