package io.quarkiverse.morphia.providers;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

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
