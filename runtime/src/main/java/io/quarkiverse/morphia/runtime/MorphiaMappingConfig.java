package io.quarkiverse.morphia.runtime;

import java.util.List;

import org.bson.UuidRepresentation;

import dev.morphia.config.DiscriminatorFunctionConverter;
import dev.morphia.config.ManualMorphiaConfig;
import dev.morphia.config.MorphiaConfig;
import dev.morphia.config.NamingStrategyConverter;
import dev.morphia.mapping.DateStorage;
import dev.morphia.mapping.DiscriminatorFunction;
import dev.morphia.mapping.MapperOptions;
import dev.morphia.mapping.NamingStrategy;
import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;

public interface MorphiaMappingConfig {
    /** The database to use. */
    @WithDefault("morphia")
    String database();

    /** Whether to apply capped collection settings from {@code @CappedAt} annotations. */
    @WithDefault("false")
    Boolean applyCaps();

    /** Whether to apply document validation settings from {@code @Validation} annotations. */
    @WithDefault("false")
    Boolean applyDocumentValidations();

    /** Whether to apply index definitions from {@code @Indexes} and {@code @Indexed} annotations. */
    @WithDefault("false")
    Boolean applyIndexes();

    /** Whether to automatically import entity models. */
    @WithDefault("true")
    Boolean autoImportModels();

    /** Whether to enable polymorphic queries. */
    @WithDefault("false")
    Boolean enablePolymorphicQueries();

    /** Whether to ignore final fields during mapping. */
    @WithDefault("false")
    Boolean ignoreFinals();

    /** Whether to store empty values in MongoDB. */
    @WithDefault("false")
    Boolean storeEmpties();

    /** Whether to store null values in MongoDB. */
    @WithDefault("false")
    Boolean storeNulls();

    /** The key to use for discriminator values. */
    @WithDefault("_t")
    String discriminatorKey();

    /** The list of packages to scan for entity classes. */
    @WithDefault(".*")
    List<String> packages();

    /** The date storage strategy (UTC or SYSTEM_DEFAULT). */
    @WithDefault("utc")
    DateStorage dateStorage();

    /** The UUID representation to use. */
    @Deprecated
    @WithDefault("standard")
    UuidRepresentation uuidRepresentation();

    /** The property discovery mode (FIELDS or METHODS). */
    @WithDefault("fields")
    MapperOptions.PropertyDiscovery propertyDiscovery();

    /**
     * The naming strategy for collection names (identity, lowerCase, snakeCase, camelCase, or kebabCase).
     */
    @WithDefault("camelCase")
    @WithConverter(NamingStrategyConverter.class)
    NamingStrategy collectionNaming();

    /**
     * The naming strategy for property names (identity, lowerCase, snakeCase, camelCase, or kebabCase).
     */
    @WithDefault("identity")
    @WithConverter(NamingStrategyConverter.class)
    NamingStrategy propertyNaming();

    /**
     * The discriminator function (className, lowerClassName, lowerSimpleName, or simpleName).
     */
    @WithDefault("simpleName")
    @WithConverter(DiscriminatorFunctionConverter.class)
    DiscriminatorFunction discriminator();

    /** Converts this config to a {@link MorphiaConfig} instance. */
    default MorphiaConfig toMorphiaConfig() {
        return ManualMorphiaConfig.configure()
                .database(database())
                .applyCaps(applyCaps())
                .applyDocumentValidations(applyDocumentValidations())
                .applyIndexes(applyIndexes())
                .autoImportModels(autoImportModels())
                .enablePolymorphicQueries(enablePolymorphicQueries())
                .ignoreFinals(ignoreFinals())
                .storeEmpties(storeEmpties())
                .storeNulls(storeNulls())
                .discriminatorKey(discriminatorKey())
                .packages(packages())
                .dateStorage(dateStorage())
                .uuidRepresentation(uuidRepresentation())
                .propertyDiscovery(propertyDiscovery())
                .collectionNaming(collectionNaming())
                .propertyNaming(propertyNaming())
                .discriminator(discriminator());
    }
}
