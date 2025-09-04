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

    private final String SECRET_KEY = "MjMxZDg5YWFjMTY1OTZlY2UwMWMxM2IzNzRiNjljNzQ3MDM4YzcxMzI3ZTJhMzIwZjUyNzdmZmE0ZDczNzI5Mw==\n"; // cheie secreta pt semnarea token
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // timpul de expirare la o ora

    public String generateToken(Person person) {
       //stocam info despre user
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", person.getRole()); //
        claims.put("id", person.getId()); //
        //construim token
        return Jwts.builder()
                .setClaims(claims) // informatii aditionale
                .setSubject(person.getUsername()) // username
                .setIssuedAt(new Date(System.currentTimeMillis())) // data emiterii
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // data expirarii
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // Sign with the secret key
                .compact(); //returnarea tokenului
    }
}
