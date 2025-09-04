package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ro.tuc.ds2020.security.JwtUserDetailsService;


import ro.tuc.ds2020.security.JwtTokenUtil;
import ro.tuc.ds2020.security.JwtRequest;
import ro.tuc.ds2020.security.JwtResponse;


//gestionez autentificarea utulizatorilor si generarea tokenurilor
@RestController
@CrossOrigin
public class JwtAuthenticationController {

    @Autowired
    @Lazy //intarzie initializarea
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    //@RequestMapping(value = "/login", method = RequestMethod.POST)
     @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
     //obtin acreditivele utilizatorului(username si pass) printr un obiect jwtrequest
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
    //verific daca is corecte
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        //incarc detaliile utilizatorului
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());
        //generarea tokenului
        final String token = jwtTokenUtil.generateToken(userDetails);
        //raspunsul ok care contine tokenul
        return ResponseEntity.ok(new JwtResponse(token));
    }


    private void authenticate(String username, String password) throws Exception {
        try {
            //creez un obiect usernamepassauth... si il trimit la authenticatemanager pt verificare
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e); //daca e dezactivat utilizatorul
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e); //daca e gresit username sau pass
        }
    }
}
