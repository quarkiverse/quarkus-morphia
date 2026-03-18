package io.quarkiverse.morphia;

import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import com.mongodb.client.MongoClient;

import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.config.MorphiaConfig;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class MorphiaRecorder {
    public Supplier<Datastore> datastoreSupplier(Supplier<MongoClient> mongoClientSupplier,
            QuarkusMorphiaConfig quarkusMorphiaConfig, List<String> entities, String clientName) {
        return () -> {
            MorphiaConfig config = quarkusMorphiaConfig.getConfig(clientName);
            Datastore datastore = Morphia.createDatastore(mongoClientSupplier.get(), config);
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            config.packages().forEach(mapPackage -> {
                try {
                    Pattern pattern = Pattern.compile(mapPackage.endsWith(".*") ? mapPackage : mapPackage + ".[A-Z]+");
                    for (String type : entities) {
                        if (pattern.matcher(type).lookingAt()) {
                            datastore.getMapper().map(contextClassLoader.loadClass(type));
                        }
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            });
            return datastore;
        };
    }
}
