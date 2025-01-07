package ro.tuc.ds2020.entities;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    // Configurarea RestTemplate-ului ca Bean
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // Metodă pentru a face un GET cu RestTemplate
    public ResponseEntity<Double> exchange(String url, HttpMethod method, Object request, Class<Double> responseType) {
        RestTemplate restTemplate = restTemplate();
        return restTemplate.exchange(url, method, null, responseType);
    }
}
