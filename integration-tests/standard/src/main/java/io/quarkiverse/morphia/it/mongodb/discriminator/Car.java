package io.quarkiverse.morphia.it.mongodb.discriminator;

import dev.morphia.annotations.Entity;

@Entity(discriminatorKey = "type", discriminator = "CAR")
public class Car extends Vehicle {
    public int seatNumber;

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }
}
