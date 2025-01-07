package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.entities.Person;
import ro.tuc.ds2020.security.JwtTokenUtil;
import ro.tuc.ds2020.security.JwtUserDetailsService;
import ro.tuc.ds2020.security.JwtRequest;
import ro.tuc.ds2020.security.JwtResponse;
import ro.tuc.ds2020.services.PersonService;
import ro.tuc.ds2020.services.TokenService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/person")
public class PersonController {

    @Autowired
    PersonService personService;
    @Autowired
    private RestTemplate restTemplate; // configurez un bean RestTemplate
    @Autowired
    TokenService tokenService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;


//    @CrossOrigin(origins = "*")
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody JwtRequest authenticationRequest) throws Exception {
//        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
//
//        // Generate JWT token after successful authentication
//        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
//        final String token = jwtTokenUtil.generateToken(userDetails);
//
//        return ResponseEntity.ok(new JwtResponse(token));  // Return token
//    }

//    @CrossOrigin(origins = "*")
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody JwtRequest authenticationRequest) {
//        try {
//            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
//            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
//            final String token = jwtTokenUtil.generateToken(userDetails);
//            return ResponseEntity.ok(new JwtResponse(token));
//        } catch (DisabledException e) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User account is disabled.");
//        } catch (BadCredentialsException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed: " + e.getMessage());
//        }
//}

//    @CrossOrigin(origins = "*")
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody JwtRequest authenticationRequest) {
//        try {
//            System.out.println("Login attempt for username: " + authenticationRequest.getUsername());
//
//            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
//            System.out.println("Authentication successful for user: " + authenticationRequest.getUsername());
//
//            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
//            System.out.println("UserDetails loaded: " + userDetails.getUsername());
//
//            final String token = jwtTokenUtil.generateToken(userDetails);
//            System.out.println("Generated token: " + token);
//
//            return ResponseEntity.ok(new JwtResponse(token));
//        } catch (DisabledException e) {
//            System.out.println("User account is disabled: " + authenticationRequest.getUsername());
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User account is disabled.");
//        } catch (BadCredentialsException e) {
//            System.out.println("Invalid credentials for user: " + authenticationRequest.getUsername());
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
//        } catch (Exception e) {
//            System.out.println("Authentication failed for user: " + authenticationRequest.getUsername() + " - Error: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed: " + e.getMessage());
//        }
//    }

//    @CrossOrigin(origins = "*")
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody JwtRequest authenticationRequest) {
//        try {
//            System.out.println("Login attempt for username: " + authenticationRequest.getUsername());
//
//            // Încarcă utilizatorul din baza de date
//            UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
//            System.out.println("UserDetails loaded: " + userDetails.getUsername());
//
//            // Compară parola din request cu parola criptată din baza de date
//            if (!passwordEncoder.matches(authenticationRequest.getPassword(), userDetails.getPassword())) {
//                System.out.println("Invalid credentials for user: " + authenticationRequest.getUsername());
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
//            }
//
//            System.out.println("Authentication successful for user: " + authenticationRequest.getUsername());
//
//            // Generează și trimite tokenul
//            final String token = jwtTokenUtil.generateToken(userDetails);
//            System.out.println("Generated token: " + token);
//
//            return ResponseEntity.ok(new JwtResponse(token));
//        } catch (DisabledException e) {
//            System.out.println("User account is disabled: " + authenticationRequest.getUsername());
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User account is disabled.");
//        } catch (BadCredentialsException e) {
//            System.out.println("Invalid credentials for user: " + authenticationRequest.getUsername());
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
//        } catch (Exception e) {
//            System.out.println("Authentication failed for user: " + authenticationRequest.getUsername() + " - Error: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed: " + e.getMessage());
//        }
//    }


//    @CrossOrigin(origins = "*")
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody JwtRequest authenticationRequest) {
//        try {
//            System.out.println("Login attempt for username: " + authenticationRequest.getUsername());
//
//            // Găsește utilizatorul din baza de date
//            Person person = personService.getByUsername(authenticationRequest.getUsername());
//            if (person == null) {
//                System.out.println("User not found for username: " + authenticationRequest.getUsername());
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
//            }
//
//            // Loghează parolele criptate pentru verificare
//            System.out.println("Password entered: " + authenticationRequest.getPassword());
//            System.out.println("Password stored in DB (hashed): " + person.getPassword());
//
//            // Compară parola introdusă cu parola stocată (hash-ul)
//            if (!passwordEncoder.matches(authenticationRequest.getPassword(), person.getPassword())) {
//                System.out.println("Passwords do not match.");
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
//            }
//
//            // Dacă parola este corectă, continuă cu autentificarea
//            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
//            final String token = jwtTokenUtil.generateToken(userDetails);
//            return ResponseEntity.ok(new JwtResponse(token));
//        } catch (Exception e) {
//            System.out.println("Authentication failed for user: " + authenticationRequest.getUsername() + " - Error: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed: " + e.getMessage());
//        }
//    }

//    public ResponseEntity<?> login(@RequestBody JwtRequest authenticationRequest) {
//        try {
//            System.out.println("Login attempt for username: " + authenticationRequest.getUsername());
//
//            // Autentificare
//            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
//            System.out.println("Authentication successful for user: " + authenticationRequest.getUsername());
//
//            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
//            System.out.println("UserDetails loaded: " + userDetails.getUsername());
//
//            final String token = jwtTokenUtil.generateToken(userDetails);
//            System.out.println("Generated token: " + token);
//
//            return ResponseEntity.ok(new JwtResponse(token));
//        } catch (DisabledException e) {
//            System.out.println("User account is disabled: " + authenticationRequest.getUsername());
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User account is disabled.");
//        } catch (BadCredentialsException e) {
//            System.out.println("Invalid credentials for user: " + authenticationRequest.getUsername());
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
//        } catch (Exception e) {
//            System.out.println("Authentication failed for user: " + authenticationRequest.getUsername() + " - Error: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed: " + e.getMessage());
//        }
//    }

    @CrossOrigin(origins = "*")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtRequest authenticationRequest) {
        try {
            System.out.println("Login attempt for username: " + authenticationRequest.getUsername());

            // Găsește utilizatorul din baza de date
            Person person = personService.getByUsername(authenticationRequest.getUsername());
            if (person == null) {
                System.out.println("User not found for username: " + authenticationRequest.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
            }

            // Loghează parolele pentru verificare
            System.out.println("Password entered: " + authenticationRequest.getPassword());
            System.out.println("Password stored in DB (hashed): " + person.getPassword());
            System.out.println("Password match result: " + passwordEncoder.matches(authenticationRequest.getPassword(), person.getPassword()));

            // Compară parola introdusă cu parola criptată (hash-ul)
            if (!passwordEncoder.matches(authenticationRequest.getPassword(), person.getPassword())) {
                System.out.println("Passwords do not match.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
            }

            // Dacă parola este corectă, continuă cu autentificarea
            System.out.println("UAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
            System.out.println("user detail:" + userDetails);
            final String token = jwtTokenUtil.generateToken(userDetails);
            System.out.println("JWT token:" + token);
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception e) {
            System.out.println("Authentication failed for user: " + authenticationRequest.getUsername() + " - Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed: " + e.getMessage());
}
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/all")
    public List<Person> getAll() {
        return personService.getAll();
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/save")
    public ResponseEntity<Person> savePerson(@RequestBody Person person) throws IOException {
        Person savedPerson = personService.savePerson(person);
        System.out.println("Saved person: " + savedPerson);
        return ResponseEntity.ok(savedPerson);
    }

//    @CrossOrigin(origins = "*")
//    @PostMapping("/update")
//    public ResponseEntity<Person> updatePerson(@RequestParam(name="id") Integer id, @RequestParam(name="username") String username, @RequestParam(name="password") String password) throws IOException {
//        Person updatedPerson = personService.updatePerson(id, username, password);
//        return ResponseEntity.ok(updatedPerson);
//    }

    @PostMapping("/update")
    public ResponseEntity<Person> updatePerson(@RequestBody Map<String, Object> updates) {
       // Integer id = (Integer) updates.get("id");
        Integer id = Integer.parseInt(updates.get("id").toString());
        String username = (String) updates.get("username");
        String password = (String) updates.get("password");
        System.out.println("inainte de actualizare");
        Person updatedPerson = personService.updatePerson(id, username, password);
        System.out.println("updated person: " + updatedPerson);
        return ResponseEntity.ok(updatedPerson);
    }


    @CrossOrigin(origins = "*")
    @GetMapping("/getbyId")
    public Person getbyId(@RequestParam(name="id") Integer id) {
        return personService.getbyId(id);
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam(name="id") Integer id) {
        personService.delete(id);
        return ResponseEntity.ok("Person deleted successfully.");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getByUsername")
    public Person getByUsername(@RequestParam(name="username") String username) {
        return personService.getByUsername(username);
    }

//    @CrossOrigin(origins = "*")
//    @DeleteMapping("/deleteUser")
//    public ResponseEntity<String> deleteUser(@RequestParam(name = "idClient") Integer idClient) {
//        String deviceServiceUrl = "http://device-service.localhost/device/devByUser?idClient=" + idClient;
//        restTemplate.delete(deviceServiceUrl);
//        personService.delete(idClient);
//        return ResponseEntity.ok("User and associated devices deleted successfully.");
//    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestParam(name = "idClient") Integer idClient) {
        try {
            String deviceServiceUrl = "http://device-service.localhost/device/devByUser?idClient=" + idClient;
            restTemplate.delete(deviceServiceUrl);
            personService.delete(idClient);
            return ResponseEntity.ok("User and associated devices deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user or associated devices: " + e.getMessage());
  }
}

}
