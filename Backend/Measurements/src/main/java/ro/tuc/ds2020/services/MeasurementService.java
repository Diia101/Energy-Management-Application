package ro.tuc.ds2020.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.entities.Measurement;
import ro.tuc.ds2020.entities.RestTemplateConfig;
import ro.tuc.ds2020.repositories.IMeasurementRepository;


import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MeasurementService {

    @Autowired
    IMeasurementRepository iMeasurementRepository;

    Double maxHour;


    @Autowired
    private RestTemplate restTemplate;

    public MeasurementService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate; // injectare corecta a RestTemplate
    }

    //toate measurements ale unui device intr o anumita zi
    public List<Measurement> getDailyMeasurements(Integer idDevice, Date date) {
        System.out.println("Date received: " + date);
        //normalizez ora in obiectul date ca sa fie 00:00:00
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date normalizedDate = calendar.getTime();
        return iMeasurementRepository.findByDeviceIdAndDate(idDevice, normalizedDate); //obtin datele
    }

    public Measurement saveMeasurement(Measurement measurement){
        return iMeasurementRepository.save(measurement);
    }

    public List<Measurement> getAll(){
        return iMeasurementRepository.findAll();
    }


    private static double sum = 0;
    private static Date lastUpdateTime = new Date();
    //    private final String deviceServiceUrl = "http://localhost:8081/device";
    private final String deviceServiceUrl = "http://device-service:8081/device";

    //valoarea maxima pe ora a unui device
    public ResponseEntity<Double> getMaxHourFromDevice(String idDevice) {
       //construiesc url cererii rest
        String url = deviceServiceUrl + "/maxHour?idDevice=" + idDevice;
       //trimit cererea
        ResponseEntity<Double> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                Double.class
        );
        //returnez rasp daca e succes, altfel codul de eroare
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity;
        } else {
            return ResponseEntity.status(responseEntity.getStatusCode()).build();
        }
    }

    //calculez suma valorilor pt fiecare ora din zi
    public Map<String, Double> getGrafic(Integer idDevice, Date date) {
        // obtin toate masuratorile pentru acest device
        List<Measurement> measurements = getMeasurementsForSpecificDate(idDevice,date);
        // map-ul pentru a pastra sumele pe fiecare ora
        Map<String, Double> hourlySums = new HashMap<>();

        // obține data curenta si setez inceputul zilei (00:00)
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // iterez prin fiecare ora din ziua curenta
        for (int i = 0; i < 24; i++) {
            // setez ora în calendar
            calendar.set(Calendar.HOUR_OF_DAY, i);

            // calculez suma pentru acea ora
            Date startOfHour = calendar.getTime();

            // calculez suma pentru ultima ora (aceasta va fi intervalul curent)
            double sumForHour = calculateSumForLastHour1(measurements, startOfHour);

            // formez un String pentru ora curenta (ex: "00:00", "01:00", ...)
            String hourKey = new SimpleDateFormat("HH:mm").format(startOfHour);

            // adaug  suma pentru acea ora în map
            hourlySums.put(hourKey, sumForHour);
        }

        // returnez rezultatul in ResponseEntity
        return hourlySums;
    }


    //calculez pt suma unei oricare ore
    // modific calculateSumForLastHour pentru a accepta un parametru de inceput al orei
    public static double calculateSumForLastHour1(List<Measurement> measurements, Date startOfHour) {
        double sum = 0;
        // setez sfarșitul orei curente (ex: 01:00:00)
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startOfHour);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date endOfHour = calendar.getTime();

        // calculez suma measurements care se afla intre inceputul si sfarsitul orei
        for (Measurement measurement : measurements) {
            if (measurement.getTimest().after(startOfHour) && measurement.getTimest().before(endOfHour)) {
                sum += measurement.getVal();
            }
        }

        return sum;
    }

    //aici calculez pt ultima ora de la ora curenta
    public static double calculateSumForLastHour(List<Measurement> measurements) {
        sum = 0;
        Date currentTime = new Date(System.currentTimeMillis()); //obtin timpu curent
        //resetarea sumei daca a trecut mai mult de 10 ore
        //timpul curent in milisecunde-timpul ultimei actulizari in milisecunde=> diferenta in milisecunde
        if (currentTime.getTime() - lastUpdateTime.getTime() >= 60 * 60 * 10000) { //reprezinta 10 ore in milisecunde
            sum = 0;
            lastUpdateTime = currentTime; //actualizez la momentu curent
        }

        //momentul de acm o ora
        Date oneHourAgo = new Date(currentTime.getTime() - 60 * 60 * 1000); //60 sec*60min*1000milisec

        for (Measurement measurement : measurements) {
            //verific daca timestamp-ul measurements e mai recent decat acm o ora
            if ( measurement.getTimest().after(oneHourAgo)) {
                sum += measurement.getVal(); //daca e in ultima ora adaugam
            }
        }
        System.out.println("Sum: " + sum);
        return sum;
}


    public List<Measurement> getMeasurementsForSpecificDate(Integer idDevice, Date date) {
        System.out.println("Date received: " + date);

        // setez ora la 00:00:00 pentru a obtine toate measurements din acea zi
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();

        // setez sfarsitul zilei (23:59:59) pentru a include toate measurements pana la sfarsitul zilei
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date endOfDay = calendar.getTime();

        // obitin masuratorile pentru dispozitivul specificat intre inceputul si sfarsitul zilei
        return iMeasurementRepository.findByIdDeviceAndTimestBetween(idDevice, startOfDay, endOfDay);
    }


}