package ro.tuc.ds2020.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.tuc.ds2020.entities.Person;
import ro.tuc.ds2020.repositories.IPersonRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PersonService {
    @Autowired
    IPersonRepository iPersonRepository;
//    @Autowired
//    IDeviceRepository ideviceRepository;

    public List<Person> getAll(){
        return iPersonRepository.findAll();
    }

    public Person savePerson(Person person){

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

        if (personOptional.isPresent()) {
            Person oldPerson = personOptional.get();
            oldPerson.setUsername(username);
            oldPerson.setPassword(password);
            return iPersonRepository.save(oldPerson);
        }
        return null;
    }


    public Person getByUsernameAndPassword(String username, String password) {
        if (iPersonRepository.getByUsernameAndPassword(username,password) != null){
            return iPersonRepository.getByUsernameAndPassword(username,password);
        }
        return null;
    }
    public Person getByUsername(String username) {
        if (iPersonRepository.getByUsername(username) != null){
            return iPersonRepository.getByUsername(username);
        }
        return null;
    }
}

