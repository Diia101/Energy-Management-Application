package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.entities.Measurement;
import ro.tuc.ds2020.entities.RestTemplateConfig;
import ro.tuc.ds2020.services.MeasurementService;

import java.util.Collections;
import java.util.Date;
import java.util.List;

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
        // Assuming idDevice is a String, as per your service method
        String url = deviceServiceUrl + "/getMaxHour?id=" + idDevice;

        ResponseEntity<Double> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                Double.class
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            List<Double> result = Collections.singletonList(responseEntity.getBody());
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(responseEntity.getStatusCode()).build();
        }
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.GET, value = "/getSum")
    @ResponseBody
    public Double getSum(@RequestParam(name = "idDevice") Integer id) {
        double sumForLastHour = 0.0;
        List<Measurement> measurements = measurementService.getAll();

        sumForLastHour = calculateSumForLastHour(measurements);
        return sumForLastHour;
}

}