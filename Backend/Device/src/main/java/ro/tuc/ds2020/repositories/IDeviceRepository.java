package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ro.tuc.ds2020.entities.Device;

import java.util.List;

@Repository
public interface IDeviceRepository extends JpaRepository<Device, Integer > {

    @Query("SELECT d FROM Device d WHERE d.idClient = :idClient")
    List<Device> findAllByClientId(Integer idClient);
}
