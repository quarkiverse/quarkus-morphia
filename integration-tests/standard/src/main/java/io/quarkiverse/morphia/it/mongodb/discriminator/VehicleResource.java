package io.quarkiverse.morphia.it.mongodb.discriminator;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import dev.morphia.Datastore;
import io.smallrye.common.annotation.Blocking;

@Path("/vehicles")
@Blocking
public class VehicleResource {
    @Inject
    @Default
    Datastore datastore;

    @GET
    public List<Vehicle> getVehicles() {
        return datastore.find(Vehicle.class).iterator().toList();
    }

    @POST
    public Response addVehicle(Vehicle vehicle) throws UnsupportedEncodingException {
        datastore.save(vehicle);
        return Response.created(URI.create("/vehicle/" + URLEncoder.encode(vehicle.name, StandardCharsets.UTF_8.toString())))
                .build();
    }
}
