package ro.tuc.ds2020.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idMeasurements;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timest;

    @Column(name = "val",nullable = false)
    private Double val;

    @Column(name = "iddevice", nullable = false)
    private int idDevice;

//    public void setIdDevice(int idDevice) {
//        this.idDevice = idDevice;
//    }
//
//    public int getIdDevice() {
//        return idDevice;
//    }

    public Measurement(Date timest, Double val, int idDevice)
    {
        this.timest = timest;
        this.val = val;
        this.idDevice = idDevice;

    }
}
