package eu.h2020.symbiote.model;

/**
 * Created by jawora on 22.09.16.
 */
public class Sensor {

    private String id;
    private String name;
    private String owner;
    private String description;

    public Sensor() {
    }

    public Sensor(String name, String owner, String description ) {
        this.name = name;
        this.owner = owner;
        this.description = description;
    }

    public Sensor(String id, String name, String owner, String description) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
