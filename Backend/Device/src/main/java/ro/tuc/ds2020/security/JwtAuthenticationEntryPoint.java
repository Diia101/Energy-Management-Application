package ro.tuc.ds2020.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

//dacă cineva încearcă să acceseze un endpoint protejat fără să fie autentificat, returnează un mesaj de eroare (Unauthorized - 401).
// se ocupa de erorile de autentificare
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
//identificare unică pentru versiunea serializabilă a clasei
   private static final long serialVersionUID = -7858869558953243875L;
//o cerere nereușită încearcă să acceseze o resursă protejată
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
