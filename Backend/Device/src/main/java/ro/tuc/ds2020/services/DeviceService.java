package ro.tuc.ds2020.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.repositories.IDeviceRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {
    @Autowired
    IDeviceRepository iDeviceRepository;

    public List<Device> getAll(){
        /*List<Device> dev = iDeviceRepository.findAll();
        List<Device> devdereturnat = new ArrayList<>();
        for (Device d: dev) {
            if(d.getId() == userid){
                devdereturnat. add (d);
            }
        }*/

        // chatgpt -> filtrare pe o lista de device, pe campu x cu lambda streams
        return iDeviceRepository.findAll();
    }
    @Autowired
    private IDeviceRepository deviceRepository;
    public List<Device> getDevicesForClient(int idClient) {
        System.out.println("id client: "+idClient);
        List<Device> allDevices = deviceRepository.findAll();
        List<Device> devicesForClient = new ArrayList<>();

        for (Device device : allDevices) {
            if (device.getIdClient() == idClient) {
                System.out.println("device: " + device);
                devicesForClient.add(device);
            }
        }

        return devicesForClient;
    }
        public Device saveDevice(Device device){

        return iDeviceRepository.save(device);
    }

    public Device getbyId(Integer id){
        if (iDeviceRepository.findById(id).isPresent()){
            return iDeviceRepository.findById(id).get();
        }
        return null;
    }

    public void delete(Integer id) {
        iDeviceRepository.deleteById(id);
    }

    public void deleteById(Integer clientId)
    {
        List<Device> devices = deviceRepository.findAllByClientId(clientId);
        if (!devices.isEmpty()) {
            deviceRepository.deleteAll(devices);
        } else {
            // pot loga un mesaj sau arunca o excepție customizată dacă vrei să tratezi cazul în mod specific
            System.out.println("No devices found for clientId: " + clientId);
        }
    }

    public Device updateDevice(Integer id, String description, String adress, Integer maxHour){
        Optional<Device> deviceOptional = iDeviceRepository.findById(id);

        if (deviceOptional.isPresent()) {
            Device oldDevice = deviceOptional.get();
            oldDevice.setDescription(description);
            oldDevice.setAdress(adress);
            return iDeviceRepository.save(oldDevice);
        }
        return null;
    }
    public int getMaxHour(Integer id) {
        Device device = iDeviceRepository.findById(id).orElseThrow(() -> new IllegalStateException("Device not found"));

        if (device != null) {
            return device.getMaxHour();
        } else {

            throw new IllegalStateException("Device is null in DeviceService.getMaxHour");
        }
    }


}

