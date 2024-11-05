package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.repositories.IDeviceRepository;
import ro.tuc.ds2020.services.DeviceService;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping(value = "/device")
public class DeviceController {
    @Autowired
    DeviceService deviceService;
    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }
    @Autowired
    IDeviceRepository iDeviceRepository;

    @CrossOrigin(origins = "*")
    @RequestMapping (method = RequestMethod.GET, value = "/all")
    @ResponseBody
    public List<Device> getAll(){
        return deviceService.getAll();
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public Device saveDevice(@RequestBody Device device) throws IOException{
        return deviceService.saveDevice(device);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.POST, value="/update")
    @ResponseBody
    public Device updateDevice(@RequestParam(name="id") Integer id,@RequestParam(name="description") String description, @RequestParam(name="adress") String adress, @RequestParam(name="maxHour") Integer maxHour) throws IOException{

        return deviceService.updateDevice(id, description, adress,maxHour);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping (method = RequestMethod.GET, value = "/getbyId")
    @ResponseBody
    public Device getbyId(@RequestParam(name="id") Integer id ){
        return deviceService.getbyId(id);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping (method = RequestMethod.DELETE, value = "/delete")
    @ResponseBody
    public void delete(@RequestParam(name="id") Integer id){
        deviceService.delete(id);

    }

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.GET, value = "/devByUser")
    @ResponseBody
    public List<Device> devByUser(@RequestParam(name = "idClient") Integer idClient) {
        List<Device> devices = deviceService.getAll();
        List<Device> devicesForClient = deviceService.getDevicesForClient(idClient);

        return devicesForClient;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteByClientId")
    @ResponseBody
    public void deleteDevicesByClientId(@RequestParam(name = "idClient") Integer idClient) {
        deviceService.deleteById(idClient);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping (method = RequestMethod.GET, value = "/getMaxHour")
    @ResponseBody
    public int getMaxHour(@RequestParam(name="id") Integer id ){

        return deviceService.getMaxHour(id);
    }




}
