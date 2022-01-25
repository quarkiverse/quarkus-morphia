package io.quarkiverse.morphia;

import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import com.mongodb.client.MongoClient;

import dev.morphia.Datastore;
import dev.morphia.Morphia;
import io.quarkiverse.morphia.runtime.MapperConfig;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class MorphiaRecorder {
    public Supplier<Datastore> datastoreSupplier(Supplier<MongoClient> mongoClientSupplier,
            MorphiaConfig morphiaConfig, List<String> entities, String clientName) {
        return () -> {
            MapperConfig config = morphiaConfig.getMapperConfig(clientName);
            Datastore datastore = Morphia.createDatastore(mongoClientSupplier.get(), config.database, config.toMapperOptions());
            try {
                if (!config.packages.isEmpty()) {
                    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                    for (String mapPackage : config.packages) {
                        Pattern pattern = Pattern.compile(mapPackage.endsWith(".*") ? mapPackage : mapPackage + ".[A-Z]+");
                        for (String type : entities) {
                            if (pattern.matcher(type).matches()) {
                                datastore.getMapper().map(contextClassLoader.loadClass(type));
                            }
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
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return datastore;
        };
    }
}
