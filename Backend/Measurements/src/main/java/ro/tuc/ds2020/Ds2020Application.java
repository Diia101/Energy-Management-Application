package ro.tuc.ds2020;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.entities.Measurement;
import ro.tuc.ds2020.services.MeasurementService;
import ro.tuc.ds2020.websocket.WebSocketController;

import java.util.Date;
import java.util.List;

@SpringBootApplication
public class Ds2020Application extends SpringBootServletInitializer {


//folosesc aici ptc e destinata exclusiv mesajelor rabbitmq; strucutra corespunde direct cu formatu json
    //si ptc nu exista dependente externe inutile sau confuzii cu alte clase din aplicatie
    static class SensorData {
        @JsonProperty("value")
        private String value; //valoarea citita din json

        @JsonProperty("id_device")
        private String idDevice; //idul dispozitivului care a trimis datele


        @Override
        public String toString() {
            return "SensorData{" +
                    "value='" + value + '\'' +
                    ", idDevice='" + idDevice + '\'' +
                    '}';
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getIdDevice() {
            return idDevice;
        }

        public void setIdDevice(String idDevice) {
            this.idDevice = idDevice;
        }
    }



    public static void main(String[] args) {
        SpringApplication.run(Ds2020Application.class, args);
    }

    @Autowired
    WebSocketController webSocketTextController;

    @Autowired
    MeasurementService measurementService;

   private RestTemplate restTemplate;

//message broker
    @RabbitListener(queues = "demoqueue") //ascult mesaje din coada
    public void run(String msg1) throws Exception { //msg1 contine mesajul primit in format json
        System.out.println(" [*] Waiting for messages. To exit, press CTRL+C");
        //convertesc mesajul json intr un obiect sensordata
        ObjectMapper objectMapper = new ObjectMapper();
        Thread.sleep(10000);
        SensorData sensorData = objectMapper.readValue(msg1, SensorData.class); //aici am obiectu json cu toate detaliile(id, data, etc)
        System.out.println(sensorData); //afisare sensor in measurement-service docker console


       // convertesc si salvez in baza de date measurement datele senzorului
        Measurement m = new Measurement(new Date(), //obiect measurement cu valori din sensordata
                Double.valueOf(sensorData.getValue()),
                Integer.parseInt(sensorData.getIdDevice()));
        measurementService.saveMeasurement(m);

        //iau toate measurements existente
        List<Measurement> measurements = measurementService.getAll();
        double sum = 0.0;

        //verific limita maxima pt websocket
        ResponseEntity<Double> maxHourResponse = measurementService.getMaxHourFromDevice(sensorData.getIdDevice());
        System.out.println("maxH:" + maxHourResponse);
        //daca limita a fost depasita trimit un mesaj websocket
        if (maxHourResponse.getStatusCode().is2xxSuccessful()) {
            Double maxHour = maxHourResponse.getBody();
            if (sum > maxHour) {
                System.out.println("nu e bine");
                webSocketTextController.sendMessage("S-a depasit bugetu");
            } else { //daca e sub limita
                System.out.println("Gj");
            }
        } else {
            System.out.println("Error retrieving maxHour from device");
        }

    }
}
