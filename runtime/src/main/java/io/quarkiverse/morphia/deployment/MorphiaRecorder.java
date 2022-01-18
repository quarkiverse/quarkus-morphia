package io.quarkiverse.morphia.deployment;

import java.util.List;
import java.util.function.Supplier;

import com.mongodb.client.MongoClient;

import dev.morphia.Datastore;
import dev.morphia.Morphia;
import io.quarkiverse.morphia.MorphiaConfig;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class MorphiaRecorder {
    public Supplier<Datastore> datastoreSupplier(Supplier<MongoClient> mongoClientSupplier,
            MorphiaConfig config, List<String> entities) {
        return () -> {
            Datastore datastore = Morphia.createDatastore(mongoClientSupplier.get(), config.database, config.toMapperOptions());
            try {
                if (config.mapEntities) {
                    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                    for (String type : entities) {
                        datastore.getMapper().map(contextClassLoader.loadClass(type));
                    }
                }
                if (config.createValidators) {
                    datastore.enableDocumentValidation();
                }
                if (config.createCaps) {
                    datastore.ensureCaps();
                }
                if (config.createIndexes) {
                    datastore.ensureIndexes();
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return datastore;
        };
    }
}
