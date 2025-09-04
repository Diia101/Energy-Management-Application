package ro.tuc.ds2020.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
//import ro.tuc.ds2020.entities.Person;
//import ro.tuc.ds2020.services.PersonService;

import java.util.Collections;
import java.util.List;

//gestionez autentificarea utilizatorilor;extrag detaliile userilor din baza de date si le prelucrez
//caută utilizatorul în baza de date pe baza username-ului
//este folosit de Spring Security pentru autentificare
@Service
public class JwtUserDetailsService implements UserDetailsService {


    @Autowired
    private PasswordEncoder passwordEncoder;//encoderul utlizat pt compararea parolelor criptate din baza de date cu cele ale users

    //incarc detalii user din baza de date pe baza username
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //iau user din baza de date
        System.out.println("usenameul primit este: " + username);
        //verific daca exista
        if (username == null || username.isEmpty()) {
            throw new UsernameNotFoundException("Username not found in token"); //
        }

        //lista de roluri pt user
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(username, "", authorities);
}
}
