package io.quarkiverse.morphia.deployment;

import java.util.function.Supplier;

import dev.morphia.Datastore;
import dev.morphia.Morphia;
import io.quarkiverse.morphia.MorphiaConfig;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class MorphiaRecorder {
    public Supplier<Datastore> configureMapperOptions(MorphiaConfig config) {
        return () -> Morphia.createDatastore(config.database, config.toMapperOptions());
    }
}
