package io.quarkiverse.morphia.it.mongodb.discriminator;

import dev.morphia.annotations.Entity;

@Entity(discriminatorKey = "type")
public abstract class Vehicle {
    public String type;
    public String name;
}
