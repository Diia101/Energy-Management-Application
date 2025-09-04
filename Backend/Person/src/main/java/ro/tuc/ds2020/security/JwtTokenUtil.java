package ro.tuc.ds2020.security;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

//creez, validez și extrag informații din token-ul JWT
//token-ul JWT este folosit pentru a menține sesiunea utilizatorului fără a-l loga de fiecare dată
@Component
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60; //durata token 5 ore

    @Value("${jwt.secret}")
    private String secret;

    //obtine username din jwt token (subject e username) cu claims
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject); //claimfortoken pt a extrage subject din token
    }

    //extrag username din token fara claim; il iau direct
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration); //calimfortoken pt a accesa expirarea
    }

    //obtin o informatie specifica din token
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token); //decodific tokenul
        return claimsResolver.apply(claims); //extrag o infromatie cum ar fi subject sau expiration
    }

    //parcurg tokenul si decodez(descompun) tokenul si obtin toate datele stocate in el
    private Claims getAllClaimsFromToken(String token) {
        //decodific toate infromatiile din token; validez semnatura cu cheia secreta si returnez infromatiile
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    //verific daca token a expirat
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token); //obtin data de expirare
        return expiration.before(new Date()); //compar cu data de acum
    }

    //generez token pt user
    public String generateToken(UserDetails userDetails) {
        //pregatesc informatiile de utilizattor
        Map<String, Object> claims = new HashMap<>(); //rolurile

        // convertesc rolurile la o lista de stringuri
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        claims.put("role", roles);

        return doGenerateToken(claims, userDetails.getUsername()); //apelez dogenerate pt a genera efectiv tokenul
    }

    //aduga info suplimentare
    private String doGenerateToken(Map<String, Object> claims, String subject) {
//claims-roluri; subject-username; Issued-data generarii; expiration- expirarea; semnatura utilizand cheia
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact(); //returnez token sub forma de string
    }

    //verific daca usernameul din token e la fel cu cel din baza de date si daca nu e expirat
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
