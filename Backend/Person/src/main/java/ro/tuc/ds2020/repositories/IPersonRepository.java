package ro.tuc.ds2020.repositories;

//import jdk.internal.misc.CarrierThreadLocal;
//import jdk.internal.misc.CarrierThreadLocal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.tuc.ds2020.entities.Person;
import java.util.Optional;

@Repository
public interface IPersonRepository extends JpaRepository<Person, Integer > {


    Person getByUsernameAndPassword(String username, String password);

    Person getByUsername(String username);

}
