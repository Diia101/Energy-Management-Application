package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.tuc.ds2020.entities.Measurement;

@Repository
public interface IMeasurementRepository extends JpaRepository<Measurement,Integer> {


}
