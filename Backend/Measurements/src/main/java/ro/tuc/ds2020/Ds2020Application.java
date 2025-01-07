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
    static class SensorData {
        @JsonProperty("value")
        private String value;

        @JsonProperty("id_device")
        private String idDevice;

        // getters and setters (or use Lombok annotations for brevity)

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


    @RabbitListener(queues = "demoqueue")
    public void run(String msg1) throws Exception {
        System.out.println(" [*] Waiting for messages. To exit, press CTRL+C");
        ObjectMapper objectMapper = new ObjectMapper();
        Thread.sleep(10000);
        SensorData sensorData = objectMapper.readValue(msg1, SensorData.class);
        System.out.println(sensorData);


//        Measurement m = new Measurement();
//        m.setTimest(new Date());
//        m.setVal(Double.valueOf(String.valueOf(sensorData.getValue())));
//        m.setIdDevice(Integer.parseInt(sensorData.getIdDevice()));
        Measurement m = new Measurement(new Date(),
                Double.valueOf(sensorData.getValue()),
                Integer.parseInt(sensorData.getIdDevice()));
        measurementService.saveMeasurement(m);


       // measurementService.saveMeasurement(m);


        List<Measurement> measurements = measurementService.getAll();
        double sum = 0.0;

        ResponseEntity<Double> maxHourResponse = measurementService.getMaxHourFromDevice(sensorData.getIdDevice());
        System.out.println("maxH:" + maxHourResponse);
        if (maxHourResponse.getStatusCode().is2xxSuccessful()) {
            Double maxHour = maxHourResponse.getBody();
            if (sum > maxHour) {
                System.out.println("nu e bine");
                webSocketTextController.sendMessage("S-a depasit bugetu");
            } else {
                System.out.println("Gj");
            }
        } else {
            System.out.println("Error retrieving maxHour from device");
        }

    }
}
