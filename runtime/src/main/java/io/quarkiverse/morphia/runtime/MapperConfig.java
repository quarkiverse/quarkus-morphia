package io.quarkiverse.morphia.runtime;

import static io.quarkiverse.morphia.runtime.Discriminator.simpleName;
import static java.util.Collections.emptyList;

import java.util.List;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;
import dev.morphia.mapping.DateStorage;
import dev.morphia.mapping.DiscriminatorFunction;
import dev.morphia.mapping.MapperOptions;
import dev.morphia.mapping.MapperOptions.PropertyDiscovery;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class MapperConfig {
    /**
     * If critter is present, auto import the generated model information created by
     * <a href="https://morphia.dev/critter/4.4/index.html">critter</a>.
     */
    @ConfigItem(defaultValue = "false")
    public boolean autoImportModels = false;

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
     * Specifies how dates should be stored in the database. This value should only be changed to support legacy systems which
     * use the {@link DateStorage#SYSTEM_DEFAULT} setting. New projects should use the default value.
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
     * List the packages to automatically map. To map any subpackages, simply include {@code .*} on the end of the name. e.g.
     * otherwise the package name will be matched exactly against the declared package for a class. If this item is
     * missing/empty, no
     * automatic mapping will be performed.
     *
     * @see Entity
     * @see Embedded
     */
    @ConfigItem
    public List<String> packages = emptyList();

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
        System.out.println("************** autoImportModels = " + autoImportModels);
        return MapperOptions.builder()
                .autoImportModels(autoImportModels)
                .collectionNaming(collectionNaming.convert())
                .dateStorage(dateStorage)
                .discriminator(discriminator.convert())
                .discriminatorKey(discriminatorKey)
                .enablePolymorphicQueries(enablePolymorphicQueries)
                .ignoreFinals(ignoreFinals)
                .mapSubPackages(mapSubPackages)
                .propertyDiscovery(propertyDiscovery)
                .propertyNaming(propertyNaming.convert())
                .storeEmpties(storeEmpties)
                .storeNulls(storeNulls)
                .build();
    }
}
