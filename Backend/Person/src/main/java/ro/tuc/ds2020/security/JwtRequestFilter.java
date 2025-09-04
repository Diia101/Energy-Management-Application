package ro.tuc.ds2020.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ro.tuc.ds2020.security.JwtUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;

////procesez fiecare cerere http si verific daca exista un token valid in headerul http
//dacă token-ul este valid, utilizatorul este autentificat și i se permite accesul la resurse protejate
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService; //incarc detaliile utilizatorului

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    //procesez cererile http si extrag token
    @Override
    //verific daca requestul contine un token valid
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        //extrag token din antetul Authorization
        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;
       //extrag token jwt si verific daca incepe cu bearer
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7); //elimin prefixul bearer
            //obtin username din token
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken); //extrag numele de utilizator din token
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }

        // verific daca user e deja autentificat
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //incarc informatiile userului din baza de date
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

            // daca token e valid si nu a expirat
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                //configurez autentificarea spring security
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }

}
