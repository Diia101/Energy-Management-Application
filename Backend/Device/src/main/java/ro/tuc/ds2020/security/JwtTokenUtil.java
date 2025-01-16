package ro.tuc.ds2020.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60; //durata token 5 ore

    @Value("${jwt.secret}")
    private String secret;

    //obtine username din jwt token (subject e username)
    public String getUsernameFromToken(String token) {
        System.out.println("getUsernameFromToken"); //
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getUserNameFromJwtToken(String token) {
        System.out.println("getUserNameFromJwtToken");
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
}

    //obtin data expirarii unui jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }


    // obtin ID-ul utilizatorului din token (daca este salvat în token)
    public Integer getIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("id", Integer.class); // Assumes "id" is stored in claims
    }

    //obtin informatii personalizate cu un resolver
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //parcurg tokenul si devodez informatiile folosind cheia secreta
    private Claims getAllClaimsFromToken(String token) {
        //decodific toate infromatiile din token; validez semnatura cu cheia secreta si returnez infromatiile
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    //verific daca token a expirat
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //generez token pt user
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // convert autoritatile la o list de stringuri
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        claims.put("role", roles);

        return doGenerateToken(claims, userDetails.getUsername());
    }
    //aduga info suplimentare
    private String doGenerateToken(Map<String, Object> claims, String subject) {
//claims-roluri; subject-username; Issued-data generarii; expiration- expirarea; semnatura utilizand cheia
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    //verific daca usernameul din token e la fel cu cel din baza de date si daca nu e expirat
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // validare  a token-ului fara UserDetails
    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
}
