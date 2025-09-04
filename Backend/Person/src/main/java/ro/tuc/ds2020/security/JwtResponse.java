package ro.tuc.ds2020.security;

import java.io.Serializable;

////e un dto olosit pentru a trimite un răspuns către client după ce un utilizator s-a autentificat
////conține token-ul JWT, care este folosit pentru autentificare în cererile viitoare
public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwttoken;

    public JwtResponse(String jwttoken) {
        this.jwttoken = jwttoken;
    }

    public String getToken() {
        return this.jwttoken;
    }
}
