package ro.tuc.ds2020.entities;

import javax.persistence.*;

@Entity
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false,unique = true)
    private String description;

    @Column(nullable = false)
    private String adress;

    // 0 - admin , 1 - user normal
    @Column(nullable = false)
    private int maxHour;

    @Column(nullable = false)
    private int idClient;

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public int getMaxHour() {
        return maxHour;
    }

    public void setMaxHour(int maxHour) {
        this.maxHour = maxHour;
    }

    public Device(int id, String description, String adress, int maxHour, int idClient) {
        this.id = id;
        this.description = description;
        this.adress = adress;
        this.maxHour = maxHour;
        this.idClient = idClient;
    }

    public Device(String description, String adress, int maxHour, int idClient) {
        this.description = description;
        this.adress = adress;
        this.maxHour = maxHour;
        this.idClient = idClient;
    }

    public Device() {
    }
}
