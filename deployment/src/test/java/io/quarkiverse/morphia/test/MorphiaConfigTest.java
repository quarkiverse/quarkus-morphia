package io.quarkiverse.morphia.test;

import static java.util.Set.of;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import dev.morphia.mapping.MapperOptions;
import io.quarkiverse.morphia.runtime.MapperConfig;

/**
 * Makes sure the Quarkus config properly exposes all the MapperOptions values minus a few unsupported/unsupportable options.
 *
 * @see MapperOptions
 */
public class MorphiaConfigTest {

    public static final Set<String> IGNORED_OPTIONS = of("classLoader", "cacheClassLookups", "fieldNaming", "queryFactory",
            "uuidRepresentation", "conventions");

    @Test
    public void ensureApiMatch() {
        Set<String> mapperNames = Arrays.stream(MapperOptions.class.getDeclaredMethods())
                .map(Method::getName)
                .filter(name -> name.startsWith("get") || name.startsWith("is"))
                .map(name -> name.startsWith("get") ? name.substring(3) : name.substring(2))
                .map(name -> name.substring(0, 1).toLowerCase(Locale.ROOT) + name.substring(1))
                .filter(name -> !IGNORED_OPTIONS.contains(name))
                .collect(Collectors.toSet());

        Set<String> fieldNames = Arrays.stream(MapperConfig.class.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());

        mapperNames.forEach(name -> assertTrue(fieldNames.contains(name), "Should find " + name + " in the config type"));
    }
}
