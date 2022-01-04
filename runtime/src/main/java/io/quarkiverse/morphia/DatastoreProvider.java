package io.quarkiverse.morphia;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import dev.morphia.Datastore;
import dev.morphia.Morphia;
import io.quarkus.arc.DefaultBean;

@Singleton
public class DatastoreProvider {

    @Produces
    @Singleton
    @DefaultBean
    public Datastore configure(MorphiaConfig config) {
        return Morphia.createDatastore("");
    }

}
