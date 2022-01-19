package io.quarkiverse.morphia;

import static io.quarkiverse.morphia.Discriminator.simpleName;
import static io.quarkus.runtime.annotations.ConfigPhase.RUN_TIME;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;
import dev.morphia.mapping.DateStorage;
import dev.morphia.mapping.DiscriminatorFunction;
import dev.morphia.mapping.MapperOptions;
import dev.morphia.mapping.MapperOptions.PropertyDiscovery;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigRoot;

@SuppressWarnings("deprecation")
@ConfigRoot(name = "morphia", phase = RUN_TIME)
public class MorphiaConfig {
    /**
     * The strategy to use when calculating collection names for entities without an explicitly mapped collection name.
     *
     * @see Entity
     */
    @ConfigItem(defaultValue = "camelCase")
    public NamingStrategy collectionNaming = NamingStrategy.camelCase;

    /**
     * Create collection caps.
     */
    @ConfigItem
    public boolean createCaps = false;

    /**
     * Create mapped indexes.
     */
    @ConfigItem
    public boolean createIndexes = false;

    /**
     * Enable mapped document validation.
     */
    @ConfigItem
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
    @ConfigItem(defaultValue = "utc")
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
    public Discriminator discriminator = simpleName;

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
    @ConfigItem(defaultValue = "true")
    public boolean ignoreFinals;

    /**
     * If true, any types annotated with {@code @Entity} or {@code @Embedded} will be mapped automatically. If this is set to
     * false, then quarkus-morphia will not attempt to create collection caps, indexes, or document validations.
     *
     * @see Entity
     * @see Embedded
     */
    @ConfigItem(defaultValue = "true")
    public boolean mapEntities = true;

    /**
     * Should "subpackages" also be mapped when mapping a specific package
     */
    @ConfigItem(defaultValue = "false")
    public boolean mapSubPackages;

    /**
     * Specifies how properties of an entity are discovered.
     *
     * @see PropertyDiscovery
     */
    @ConfigItem(defaultValue = "fields")
    public PropertyDiscovery propertyDiscovery = PropertyDiscovery.FIELDS;

    /**
     * The strategy to use when calculating collection names for entities without an explicitly mapped property name.
     *
     * @see Property
     */
    @ConfigItem(defaultValue = "identity")
    public NamingStrategy propertyNaming = NamingStrategy.identity;

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
        return MapperOptions.builder()
                .collectionNaming(collectionNaming.convert())
                .dateStorage(dateStorage)
                .discriminator(discriminator.convert())
                .discriminatorKey(discriminatorKey)
                .enablePolymorphicQueries(enablePolymorphicQueries)
                .ignoreFinals(this.ignoreFinals)
                .mapSubPackages(mapSubPackages)
                .propertyDiscovery(propertyDiscovery)
                .propertyNaming(propertyNaming.convert())
                .storeEmpties(storeEmpties)
                .storeNulls(storeNulls)
                .build();
    }
}
