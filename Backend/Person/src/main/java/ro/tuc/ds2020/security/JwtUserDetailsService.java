package ro.tuc.ds2020.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.entities.Person;
import ro.tuc.ds2020.services.PersonService;

import java.util.Collections;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;

//gestionez autentificarea utilizatorilor;extrag detaliile userilor din baza de date si le prelucrez
//caută utilizatorul în baza de date pe baza username-ului
//este folosit de Spring Security pentru autentificare@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    PersonService personService; //ppt a accesa user din baza de date
    @Autowired
    private PasswordEncoder passwordEncoder;//encoderul utlizat pt compararea parolelor criptate din baza de date cu cele ale users

    //incarc detalii user din baza de date pe baza username
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       //iau user din baza de date
        Person person = personService.getByUsername(username);
        //verific daca exista
        if (person == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        //lista de roluri pt user
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + person.getRole()));
        return new User(person.getUsername(), person.getPassword(), authorities); //creez obiect user
    }
}