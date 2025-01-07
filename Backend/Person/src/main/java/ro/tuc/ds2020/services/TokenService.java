package ro.tuc.ds2020.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.entities.Person;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {

    private final String SECRET_KEY = "your_secret_key"; // Use a strong secret key
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour in milliseconds

    public String generateToken(Person person) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", person.getRole()); // Assuming you have a method to get the role
        claims.put("id", person.getId()); // Assuming you have a method to get the ID

        return Jwts.builder()
                .setClaims(claims) // Set the claims
                .setSubject(person.getUsername()) // The username as the subject
                .setIssuedAt(new Date(System.currentTimeMillis())) // Issue date
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Expiration date
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // Sign with the secret key
                .compact(); // Build the token
    }
}
