package ro.tuc.ds2020.entities;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    // configurarea RestTemplate-ului ca Bean
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // metoda pentru a face un GET cu RestTemplate
    //url catre care se face cererea, tipul cererii(method), corpul cererii(request) si tipul raspunsului
    public ResponseEntity<Double> exchange(String url, HttpMethod method, Object request, Class<Double> responseType) {
        RestTemplate restTemplate = restTemplate(); //creez instanta de restTemplate
        return restTemplate.exchange(url, method, null, responseType);
    }
}
