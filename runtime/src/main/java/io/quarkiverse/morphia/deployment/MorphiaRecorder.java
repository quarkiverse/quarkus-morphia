package io.quarkiverse.morphia.deployment;

import java.util.function.Supplier;

import com.mongodb.client.MongoClient;

import dev.morphia.Datastore;
import dev.morphia.Morphia;
import io.quarkiverse.morphia.MorphiaConfig;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class MorphiaRecorder {
    public Supplier<Datastore> datastoreSupplier(Supplier<MongoClient> mongoClientSupplier,
            MorphiaConfig config) {
        return () -> Morphia.createDatastore(mongoClientSupplier.get(), config.database, config.toMapperOptions());
    }
}
