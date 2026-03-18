package io.quarkiverse.morphia;

import java.util.Map;

import dev.morphia.config.MorphiaConfig;
import io.quarkiverse.morphia.runtime.MorphiaMappingConfig;
import io.quarkus.mongodb.runtime.MongoClientBeanUtil;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithParentName;

@ConfigRoot(phase = ConfigPhase.RUN_TIME)
@ConfigMapping(prefix = "quarkus.morphia")
public interface QuarkusMorphiaConfig {
    /**
     * The default Mapper configuration.
     */
    @WithParentName
    MorphiaMappingConfig defaultConfig();

    /**
     * Configures additional {@code @Mapper} configurations.
     * <p>
     * each cluster has a unique identifier which must be identified to select the right connection.
     * example:
     *
     * <pre>
     * {@code
     * quarkus.morphia.cluster1.discriminatorKey = className
     * quarkus.morphia.cluster2.discriminatorKey = _type
     * }
     * </pre>
     */
    @WithParentName
    Map<String, MorphiaMappingConfig> namedConfigs();

    default MorphiaConfig getConfig(String clientName) {
        if (MongoClientBeanUtil.isDefault(clientName)) {
            return defaultConfig().toMorphiaConfig();
        }
        Map<String, MorphiaMappingConfig> named = namedConfigs();
        MorphiaMappingConfig mappingConfig = named.containsKey(clientName) ? named.get(clientName) : defaultConfig();
        return mappingConfig.toMorphiaConfig();
    }
}
