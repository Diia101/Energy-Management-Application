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

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    PersonService personService;

    @Autowired
    private PasswordEncoder passwordEncoder;  // Injectează PasswordEncoder pentru a compara parolele

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = personService.getByUsername(username);

        if (person == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Verifică dacă parola trimisă de utilizator se potrivește cu cea criptată din baza de date
//        if (!passwordEncoder.matches(person.getPassword(), person.getPassword())) {
//            throw new UsernameNotFoundException("Invalid credentials");
//        }

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + person.getRole()));
        return new User(person.getUsername(), person.getPassword(), authorities);
    }


    // @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        System.out.println("Căutăm utilizatorul: " + username);  // Mesaj de diagnosticare
//
//        Person person = personService.getByUsername(username);
//
//        if (person == null) {
//            System.out.println("Utilizatorul nu a fost găsit: " + username);  // Dacă nu găsim utilizatorul
//            throw new UsernameNotFoundException("User not found with username: " + username);
//        } else {
//            System.out.println("Utilizatorul găsit: " + username + " cu parola: " + person.getPassword());  // Dacă găsim utilizatorul
//
//            // Verificăm dacă parola introdusă corespunde cu cea criptată stocată în baza de date
//            if (!passwordEncoder.matches(person.getPassword(), person.getPassword())) {
//                System.out.println("Parola nu se potrivește pentru utilizatorul: " + username);
//                throw new UsernameNotFoundException("Invalid credentials");
//            }
//
//            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + person.getRole()));
//            return new User(person.getUsername(), person.getPassword(), authorities);
//        }
//    }
}