package io.quarkiverse.morphia.it.mongodb.discriminator;

import dev.morphia.annotations.Entity;

@Entity(discriminatorKey = "type")
public abstract class Vehicle {
    public String type;
    public String name;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
