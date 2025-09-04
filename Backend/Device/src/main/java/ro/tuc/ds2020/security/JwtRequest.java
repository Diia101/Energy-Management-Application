package ro.tuc.ds2020.security;

import java.io.Serializable;

//este modelul folosit pentru autentificare
//conține username și password, pe care frontend-ul le trimite când cineva se loghează
//dto pt a transporta datele de autentificare de la frontend catre backend
public class JwtRequest implements Serializable {
//identificator unic al clasei pentru procesul de serializare
    private static final long serialVersionUID = 5926468583005150707L;

    private String username;
    private String password;


    public JwtRequest()
    {

    }

    public JwtRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
