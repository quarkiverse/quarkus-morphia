package io.quarkiverse.morphia.providers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

import org.eclipse.microprofile.config.Config;

import dev.morphia.mapping.MapperOptions;

public class Producers {

    @Produces
    @Named("critter")
    @ApplicationScoped
    MapperOptions datastore(Config config) {
        return MapperOptions.DEFAULT;
    }
}
