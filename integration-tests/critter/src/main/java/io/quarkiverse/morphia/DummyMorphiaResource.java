package io.quarkiverse.morphia;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

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
