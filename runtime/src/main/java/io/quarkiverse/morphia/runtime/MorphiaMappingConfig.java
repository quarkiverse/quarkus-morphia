package io.quarkiverse.morphia.runtime;

import java.util.List;
import java.util.Optional;

import org.bson.UuidRepresentation;

import dev.morphia.config.ManualMorphiaConfig;
import dev.morphia.config.MorphiaConfig;
import dev.morphia.mapping.DateStorage;
import dev.morphia.mapping.DiscriminatorFunction;
import dev.morphia.mapping.MapperOptions;
import dev.morphia.mapping.NamingStrategy;

public interface MorphiaMappingConfig {
    /** The database to use. */
    Optional<String> database();

    /** Whether to apply capped collection settings from {@code @CappedAt} annotations. */
    Optional<Boolean> applyCaps();

    /** Whether to apply document validation settings from {@code @Validation} annotations. */
    Optional<Boolean> applyDocumentValidations();

    /** Whether to apply index definitions from {@code @Indexes} and {@code @Indexed} annotations. */
    Optional<Boolean> applyIndexes();

    /** Whether to automatically import entity models. */
    Optional<Boolean> autoImportModels();

    /** Whether to enable polymorphic queries. */
    Optional<Boolean> enablePolymorphicQueries();

    /** Whether to ignore final fields during mapping. */
    Optional<Boolean> ignoreFinals();

    /** Whether to store empty values in MongoDB. */
    Optional<Boolean> storeEmpties();

    /** Whether to store null values in MongoDB. */
    Optional<Boolean> storeNulls();

    /** The key to use for discriminator values. */
    Optional<String> discriminatorKey();

    /** The list of packages to scan for entity classes. */
    Optional<List<String>> packages();

    /** The date storage strategy (UTC or SYSTEM_DEFAULT). */
    Optional<DateStorage> dateStorage();

    /** The UUID representation to use. */
    Optional<UuidRepresentation> uuidRepresentation();

    /** The property discovery mode (FIELDS or METHODS). */
    Optional<MapperOptions.PropertyDiscovery> propertyDiscovery();

    /**
     * The naming strategy for collection names (identity, lowerCase, snakeCase, camelCase, or kebabCase).
     */
    Optional<String> collectionNaming();

    /**
     * The naming strategy for property names (identity, lowerCase, snakeCase, camelCase, or kebabCase).
     */
    Optional<String> propertyNaming();

    /**
     * The discriminator function (className, lowerClassName, lowerSimpleName, or simpleName).
     */
    Optional<String> discriminator();

    /** Converts this config to a {@link MorphiaConfig} instance. */
    default MorphiaConfig toMorphiaConfig() {
        MorphiaConfig config = ManualMorphiaConfig.configure();
        if (database().isPresent()) {
            config = config.database(database().get());
        }
        if (applyCaps().isPresent()) {
            config = config.applyCaps(applyCaps().get());
        }
        if (applyDocumentValidations().isPresent()) {
            config = config.applyDocumentValidations(applyDocumentValidations().get());
        }
        if (applyIndexes().isPresent()) {
            config = config.applyIndexes(applyIndexes().get());
        }
        if (autoImportModels().isPresent()) {
            config = config.autoImportModels(autoImportModels().get());
        }
        if (enablePolymorphicQueries().isPresent()) {
            config = config.enablePolymorphicQueries(enablePolymorphicQueries().get());
        }
        if (ignoreFinals().isPresent()) {
            config = config.ignoreFinals(ignoreFinals().get());
        }
        if (storeEmpties().isPresent()) {
            config = config.storeEmpties(storeEmpties().get());
        }
        if (storeNulls().isPresent()) {
            config = config.storeNulls(storeNulls().get());
        }
        if (discriminatorKey().isPresent()) {
            config = config.discriminatorKey(discriminatorKey().get());
        }
        if (packages().isPresent()) {
            config = config.packages(packages().get());
        }
        if (dateStorage().isPresent()) {
            config = config.dateStorage(dateStorage().get());
        }
        if (uuidRepresentation().isPresent()) {
            config = config.uuidRepresentation(uuidRepresentation().get());
        }
        if (propertyDiscovery().isPresent()) {
            config = config.propertyDiscovery(propertyDiscovery().get());
        }
        if (collectionNaming().isPresent()) {
            config = config.collectionNaming(toNamingStrategy(collectionNaming().get()));
        }
        if (propertyNaming().isPresent()) {
            config = config.propertyNaming(toNamingStrategy(propertyNaming().get()));
        }
        if (discriminator().isPresent()) {
            config = config.discriminator(toDiscriminatorFunction(discriminator().get()));
        }
        return config;
    }

    private static NamingStrategy toNamingStrategy(String name) {
        return switch (name.toLowerCase()) {
            case "identity" -> NamingStrategy.identity();
            case "lowercase", "lower_case", "lower-case" -> NamingStrategy.lowerCase();
            case "snakecase", "snake_case", "snake-case" -> NamingStrategy.snakeCase();
            case "camelcase", "camel_case", "camel-case" -> NamingStrategy.camelCase();
            case "kebabcase", "kebab_case", "kebab-case" -> NamingStrategy.kebabCase();
            default -> throw new IllegalArgumentException("Unknown naming strategy: " + name);
        };
    }

    private static DiscriminatorFunction toDiscriminatorFunction(String name) {
        return switch (name.toLowerCase()) {
            case "classname", "class_name", "class-name" -> DiscriminatorFunction.className();
            case "lowerclassname", "lower_class_name", "lower-class-name" -> DiscriminatorFunction.lowerClassName();
            case "lowersimplename", "lower_simple_name", "lower-simple-name" -> DiscriminatorFunction.lowerSimpleName();
            case "simplename", "simple_name", "simple-name" -> DiscriminatorFunction.simpleName();
            default -> throw new IllegalArgumentException("Unknown discriminator function: " + name);
        };
    }
}
