package ro.tuc.ds2020.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.tuc.ds2020.entities.Person;
import ro.tuc.ds2020.repositories.IPersonRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PersonService {
    @Autowired
    IPersonRepository iPersonRepository;


    @Autowired
    PasswordEncoder encoder;

    public List<Person> getAll(){
        return iPersonRepository.findAll();
    }


    public Person savePerson(Person person) {
        // criptez parola inainte de a o salva
        System.out.println(person);
        person.setPassword(encoder.encode(person.getPassword())); //criptare
        System.out.println(person.getPassword());
        return iPersonRepository.save(person);
    }


    public Person getbyId(Integer id){
        if (iPersonRepository.findById(id).isPresent()){
            return iPersonRepository.findById(id).get();
        }
        return null;
    }

    public void delete(Integer id) {
        iPersonRepository.deleteById(id);
    }

    public Person updatePerson(Integer id, String username, String password){
        Optional<Person> personOptional = iPersonRepository.findById(id);
        System.out.println("pers optional:"+ personOptional);
        if (personOptional.isPresent()) { //daca user e in baza de date
            Person oldPerson = personOptional.get();
            oldPerson.setUsername(username);
            // oldPerson.setPassword(password);
            System.out.println(oldPerson);
            oldPerson.setPassword(encoder.encode(password)); //criptez inainte de salvare
            System.out.println(oldPerson.getPassword());
            return iPersonRepository.save(oldPerson);
        }
        return null;
    }


    public Person getByUsernameAndPassword(String username, String password) {
        if (iPersonRepository.getByUsernameAndPassword(username,password) != null){ //gasesc pe baza username si pass
            return iPersonRepository.getByUsernameAndPassword(username,password);
        }
        return null;
    }
    //gaseste user pe baza username
    public Person getByUsername(String username) {
        System.out.println("username e: "+ username);
        if (iPersonRepository.getByUsername(username) != null){
            return iPersonRepository.getByUsername(username);
        }
        return null;
}
}