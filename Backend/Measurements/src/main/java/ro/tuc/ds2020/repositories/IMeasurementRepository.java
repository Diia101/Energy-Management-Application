package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.tuc.ds2020.entities.Measurement;

import java.util.Date;
import java.util.List;


@Repository
public interface IMeasurementRepository extends JpaRepository<Measurement,Integer>{
    //returnez toate measurements ale unui device pt o anumita zi
    @Query("SELECT m FROM Measurement m WHERE m.idDevice = :idDevice AND DATE(m.timest) = DATE(:date)")
    List<Measurement> findByDeviceIdAndDate(@Param("idDevice") Integer idDevice, @Param("date") Date date);

    // returnez toate measurements ale unui device intr un interval de timp
    List<Measurement> findByIdDeviceAndTimestBetween(Integer idDevice, Date startDate, Date endDate);
}
