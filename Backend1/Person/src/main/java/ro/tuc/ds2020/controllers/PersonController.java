package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ro.tuc.ds2020.entities.Person;
import ro.tuc.ds2020.services.PersonService;
import ro.tuc.ds2020.services.TokenService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping(value = "/person")
@RestController
public class PersonController {
    @Autowired
    PersonService personService;
    @Autowired
    private RestTemplate restTemplate; // configurez un bean RestTemplate
    @Autowired
    TokenService tokenService;


    @CrossOrigin(origins = "*")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        Person person = personService.getByUsernameAndPassword(username, password);

        if (person != null) {
            String token = tokenService.generateToken(person); // Generate the token
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }

    private String generateToken(Person person) {
        // Logic to generate JWT token using your preferred library (e.g., JWT)
        // This typically includes encoding the user's info and signing the token
        return "generated-jwt-token"; // Replace with actual JWT generation logic
    }




    @CrossOrigin(origins = "*")
    @RequestMapping (method = RequestMethod.GET, value = "/all")
    @ResponseBody
    public List<Person> getAll(){
        return personService.getAll();
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public Person savePerson(@RequestBody Person person) throws IOException{
         return personService.savePerson(person);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.POST, value="/update")
    @ResponseBody
    public Person updatePerson(@RequestParam(name="id") Integer id,@RequestParam(name="username") String username, @RequestParam(name="password") String password) throws IOException{

         return personService.updatePerson(id, username, password);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping (method = RequestMethod.GET, value = "/getbyId")
    @ResponseBody
    public Person getbyId(@RequestParam(name="id") Integer id ){
        return personService.getbyId(id);
    }

    @CrossOrigin(origins = "*")
        @RequestMapping (method = RequestMethod.DELETE, value = "/delete")
        @ResponseBody
        public void delete(@RequestParam(name="id") Integer id){
          personService.delete(id);

    }

    @CrossOrigin(origins = "*")
    @RequestMapping (method = RequestMethod.GET, value = "/getByUsernameAndPassword")
    @ResponseBody
    public Person getByUsernameAndPassword(@RequestParam(name="username") String username, @RequestParam(name="password") String password  ){
        return personService.getByUsernameAndPassword(username, password);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping (method = RequestMethod.GET, value = "/getByUsername")
    @ResponseBody
    public Person getByUsername(@RequestParam(name="username") String username ){
        return personService.getByUsername(username);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping (method = RequestMethod.DELETE, value = "/deleteUser")
    @ResponseBody
    public void deleteUser(@RequestParam(name = "idClient") Integer idClient) {

        String deviceServiceUrl = "http://localhost:8081/device/devByUser?idClient=" + idClient;
        restTemplate.delete(deviceServiceUrl);

        personService.delete(idClient);

    }


}
