package ro.tuc.ds2020.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.entities.Measurement;
import ro.tuc.ds2020.entities.RestTemplateConfig;
import ro.tuc.ds2020.repositories.IMeasurementRepository;


import java.util.Date;
import java.util.List;

@Service
public class MeasurementService {

    @Autowired
    IMeasurementRepository iMeasurementRepository;

    Double maxHour;

//    public MeasurementService(RestTemplateConfig restTemplate) {
//        this.restTemplate = restTemplate;
//    }

    @Autowired
    private RestTemplate restTemplate; // Declarația corectă a RestTemplate

    public MeasurementService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate; // Injectare corectă a RestTemplate
    }


    public Measurement saveMeasurement(Measurement measurement){
        return iMeasurementRepository.save(measurement);
    }

    public List<Measurement> getAll(){
        return iMeasurementRepository.findAll();
    }


    //private final RestTemplateConfig restTemplate;
    private static double sum = 0;
    private static Date lastUpdateTime = new Date();
    //    private final String deviceServiceUrl = "http://localhost:8081/device";
    private final String deviceServiceUrl = "http://device-service:8081/device";
    public ResponseEntity<Double> getMaxHourFromDevice(String idDevice) {
        String url = deviceServiceUrl + "/maxHour?idDevice=" + idDevice;
        ResponseEntity<Double> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                Double.class
        );
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity;
        } else {
            return ResponseEntity.status(responseEntity.getStatusCode()).build();
        }
    }
    public static double calculateSumForLastHour(List<Measurement> measurements) {
        sum = 0;
        Date currentTime = new Date(System.currentTimeMillis());
        if (currentTime.getTime() - lastUpdateTime.getTime() >= 60 * 60 * 10000) {
            sum = 0;
            lastUpdateTime = currentTime;
        }


        Date oneHourAgo = new Date(currentTime.getTime() - 60 * 60 * 1000);

        for (Measurement measurement : measurements) {
            if ( measurement.getTimest().after(oneHourAgo)) {
                sum += measurement.getVal();
            }
        }
        System.out.println("Sum: " + sum);
        return sum;
}

}