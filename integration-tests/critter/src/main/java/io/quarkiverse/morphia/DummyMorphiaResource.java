package io.quarkiverse.morphia;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import dev.morphia.Datastore;
import dev.morphia.mapping.MapperOptions;

@Path("/dummy")
@ApplicationScoped
public class DummyMorphiaResource {
    @Inject
    @Default
    Datastore datastore;

    @Inject
    MapperOptions alternate;

    @GET
    public Response addBook() {
        return Response.ok().build();
    }
}
