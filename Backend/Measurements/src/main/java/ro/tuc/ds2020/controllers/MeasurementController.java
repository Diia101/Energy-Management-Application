package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.entities.Measurement;
import ro.tuc.ds2020.entities.RestTemplateConfig;
import ro.tuc.ds2020.services.MeasurementService;

import java.util.*;

import static ro.tuc.ds2020.services.MeasurementService.calculateSumForLastHour;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping(value = "/measurement")
public class MeasurementController {
    //    private final String deviceServiceUrl = "http://localhost:8081/device";
    private final String deviceServiceUrl = "http://device-service:8081/device";
    private final RestTemplateConfig restTemplate;
    @Autowired
    MeasurementService measurementService;

    public MeasurementController(RestTemplateConfig restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.GET, value = "/all")
    @ResponseBody
    public List<Measurement> getAll() {
        return measurementService.getAll();
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.GET, value = "/getMaxHourFromDevice")
    @ResponseBody
    public ResponseEntity<List<Double>> getMaxHourFromDevice(@RequestParam(name = "idDevice") String idDevice) {
        //construiesc url complet pt a face apel spre rest
        String url = deviceServiceUrl + "/getMaxHour?id=" + idDevice;

        //trimite o cerere get catre url specificat utilizand restTemplate
        ResponseEntity<Double> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null, //nu trimit niciun corp al cererii
                Double.class //astept un raspuns de tip double
        );
        //daca e succes
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            List<Double> result = Collections.singletonList(responseEntity.getBody()); //pun rasp intr o lista
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(responseEntity.getStatusCode()).build();
        }
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.GET, value = "/getSum")
    @ResponseBody
    public Double getSum(@RequestParam(name = "idDevice") Integer idDevice){
        double sumForLastHour = 0.0;
        List<Measurement> measurements = measurementService.getAll(); //toate measurements
        sumForLastHour = calculateSumForLastHour(measurements); //suma pt ultima ora
        return sumForLastHour;
}

    //pt grafic
    @CrossOrigin(origins = "*")
    ///@GetMapping("/getDailyData")
    @RequestMapping(method = RequestMethod.GET, value = "/getDailyData")
    @ResponseBody
    public Map<String, Double> getDailyData(
            @RequestParam(name = "idDevice", required = true) Integer idDevice,
            @RequestParam(name = "date", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        Map<String, Double> rezultat = new HashMap<>();
        try {
            //daca parametrii lipsesc adaug o eroare
            if (idDevice == null || date == null) {
                System.out.println("Parametri lipsă: idDevice sau date");
                rezultat.put("error", -1.0); //eroarea
                return rezultat;
            }

            System.out.println("idDevice: " + idDevice + ", date: " + date);

            // apelez serviciul pentru a obține datele zilnice
            rezultat = measurementService.getGrafic(idDevice, date);

            // verificare rezultat
            if (rezultat == null || rezultat.isEmpty()) {
                System.out.println("Niciun rezultat găsit pentru parametrii furnizați.");
                rezultat.put("error", 0.0); // indica lipsa datelor
            }

        } catch (Exception e) {
            // Gestionare erori neașteptate
            e.printStackTrace();
            System.out.println("A apărut o eroare în procesarea cererii.");
            rezultat.put("error", -2.0); // indica eroare interna
        }

        System.out.println("Rezultat final: " + rezultat);
        return rezultat;
    }



}