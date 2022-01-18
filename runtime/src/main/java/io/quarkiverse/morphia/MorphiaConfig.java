package io.quarkiverse.morphia;

import static dev.morphia.mapping.DiscriminatorFunction.className;
import static dev.morphia.mapping.DiscriminatorFunction.lowerClassName;
import static dev.morphia.mapping.DiscriminatorFunction.lowerSimpleName;
import static dev.morphia.mapping.DiscriminatorFunction.simpleName;
import static dev.morphia.mapping.NamingStrategy.camelCase;
import static dev.morphia.mapping.NamingStrategy.identity;
import static dev.morphia.mapping.NamingStrategy.kebabCase;
import static dev.morphia.mapping.NamingStrategy.lowerCase;
import static dev.morphia.mapping.NamingStrategy.snakeCase;
import static io.quarkus.runtime.annotations.ConfigPhase.RUN_TIME;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;
import dev.morphia.mapping.DateStorage;
import dev.morphia.mapping.DiscriminatorFunction;
import dev.morphia.mapping.MapperOptions;
import dev.morphia.mapping.MapperOptions.Builder;
import dev.morphia.mapping.MapperOptions.PropertyDiscovery;
import dev.morphia.mapping.NamingStrategy;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "morphia", phase = RUN_TIME)
public class MorphiaConfig {
    /**
     * The strategy to use when calculating collection names for entities without an explicitly mapped collection name.
     * Possible values include:
     * <ol>
     * <ol>
     * built-in functions defined on {@link NamingStrategy}
     * </ol>
     * <ol>
     * the class names of a type extending {@link NamingStrategy}
     * </ol>
     * </ol>
     *
     * @see Entity
     */
    @ConfigItem(defaultValue = "camelCase")
    public String collectionNaming = "camelCase";

    /**
     * Create collection caps.
     */
    @ConfigItem(name = "create.caps")
    public boolean createCaps = false;

    /**
     * Create mapped indexes.
     */
    @ConfigItem(name = "create.indexes")
    public boolean createIndexes = false;

    /**
     * Enable mapped document validation.
     */
    @ConfigItem(name = "create.validators")
    public boolean createValidators = false;

    /**
     * The database to use
     */
    @ConfigItem
    public String database;

    /**
     * Specifies how dates should be stored in the database. Possible values are the names of enum values as
     * defined in {@link DateStorage}.
     *
     * @see DateStorage
     */
    @ConfigItem(defaultValue = "UTC")
    public DateStorage dateStorage = DateStorage.UTC;

    /**
     * The function to use when calculating an entity's discriminator value. Possible values include:
     * <ul>
     * <ol>
     * built-in functions defined on {@link DiscriminatorFunction}
     * </ol>
     * <ol>
     * the class names of a type extending {@link DiscriminatorFunction}
     * </ol>
     * </ul>
     *
     * @see DiscriminatorFunction
     */
    @ConfigItem(defaultValue = "simpleName")
    public String discriminator = "simpleName";

    /**
     * The key to use when storing an entity's discriminator value
     */
    @ConfigItem(defaultValue = "_t")
    public String discriminatorKey = "_t";

    /**
     * Should queries be updated to include subtypes when querying for a specific type
     */
    @ConfigItem(defaultValue = "true")
    public boolean enablePolymorphicQueries;

    /**
     * Should final properties be serialized
     */
    @ConfigItem(defaultValue = "false")
    public boolean ignoreFinals;

    /**
     * Should "subpackages" also be mapped when mapping a specific package
     */
    @ConfigItem(defaultValue = "false")
    public boolean mapSubPackages;

    /**
     * If true, any types annotated with {@code @Entity} or {@code @Embedded} will be mapped automatically.
     * 
     * @see Entity
     * @see Embedded
     */
    @ConfigItem(name = "map.entities", defaultValue = "true")
    public boolean mapEntities = true;

    /**
     * Specifies how properties of an entity are discovered. Possible values are the names of enum
     * values as defined in {@link PropertyDiscovery}.
     *
     * @see PropertyDiscovery
     */
    @ConfigItem(defaultValue = "fields")
    public PropertyDiscovery propertyDiscovery = PropertyDiscovery.FIELDS;

    /**
     * The strategy to use when calculating collection names for entities without an explicitly mapped collection name.
     * Possible values include:
     * <ol>
     * <ol>
     * built-in functions defined on {@link NamingStrategy}
     * </ol>
     * <ol>
     * the class names of a type extending {@link NamingStrategy}
     * </ol>
     * </ol>
     *
     * @see Property
     */
    @ConfigItem(defaultValue = "identity")
    public String propertyNaming = "identity";

    /**
     * Should empty Lists/Maps/Sets be serialized
     */
    @ConfigItem(defaultValue = "false")
    public boolean storeEmpties;

    /**
     * Should null properties be serialized
     */
    @ConfigItem(defaultValue = "false")
    public boolean storeNulls;

    public MapperOptions toMapperOptions() {
        Builder builder = MapperOptions.builder()
                .ignoreFinals(this.ignoreFinals)
                .storeNulls(storeNulls)
                .storeEmpties(storeEmpties)
                .mapSubPackages(mapSubPackages)
                .enablePolymorphicQueries(enablePolymorphicQueries)
                .dateStorage(dateStorage)
                .discriminatorKey(discriminatorKey)
                .propertyDiscovery(propertyDiscovery);

        if ("className".equals(discriminator)) {
            builder.discriminator(className());
        } else if ("lowerClassName".equals(discriminator)) {
            builder.discriminator(lowerClassName());
        } else if ("lowerSimpleName".equals(discriminator)) {
            builder.discriminator(lowerSimpleName());
        } else if ("simpleName".equals(discriminator)) {
            builder.discriminator(simpleName());
        } else {
            throw new IllegalArgumentException(discriminator + " not a valid value for the discriminator function");
        }

        if ("identity".equals(collectionNaming)) {
            builder.collectionNaming(identity());
        } else if ("lowerCase".equals(collectionNaming)) {
            builder.collectionNaming(lowerCase());
        } else if ("snakeCase".equals(collectionNaming)) {
            builder.collectionNaming(snakeCase());
        } else if ("camelCase".equals(collectionNaming)) {
            builder.collectionNaming(camelCase());
        } else if ("kebabCase".equals(collectionNaming)) {
            builder.collectionNaming(kebabCase());
        } else {
            throw new IllegalArgumentException(collectionNaming + " not a valid value for the collection naming strategy");
        }

        if ("identity".equals(propertyNaming)) {
            builder.propertyNaming(identity());
        } else if ("lowerCase".equals(propertyNaming)) {
            builder.propertyNaming(lowerCase());
        } else if ("snakeCase".equals(propertyNaming)) {
            builder.propertyNaming(snakeCase());
        } else if ("camelCase".equals(propertyNaming)) {
            builder.propertyNaming(camelCase());
        } else if ("kebabCase".equals(propertyNaming)) {
            builder.propertyNaming(kebabCase());
        } else {
            throw new IllegalArgumentException(propertyNaming + " not a valid value for the property naming strategy");
        }

        return builder.build();
    }
}
