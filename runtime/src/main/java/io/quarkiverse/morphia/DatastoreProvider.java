package io.quarkiverse.morphia;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.mongodb.client.MongoClient;

import dev.morphia.Datastore;
import dev.morphia.Morphia;
import io.quarkus.arc.DefaultBean;

@Singleton
public class DatastoreProvider {

    @Inject
    private MongoClient client;

    @Produces
    @Singleton
    @DefaultBean
    public Datastore configure(MorphiaConfig config) {
        Datastore datastore = Morphia.createDatastore(client, config.database, config.toMapperOptions());
        datastore.getMapper().mapPackage("io.quarkiverse.morphia.it.models");
        return datastore;
    }

}
