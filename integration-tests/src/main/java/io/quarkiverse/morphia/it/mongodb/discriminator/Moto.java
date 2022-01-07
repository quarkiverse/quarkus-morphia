package io.quarkiverse.morphia.it.mongodb.discriminator;

import dev.morphia.annotations.Entity;

@Entity(discriminatorKey = "type", discriminator = "MOTO")
public class Moto extends Vehicle {
    public boolean sideCar;
}
