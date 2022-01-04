package io.quarkiverse.morphia.deployment;

import javax.inject.Singleton;

import dev.morphia.Datastore;
import io.quarkiverse.morphia.DatastoreProvider;
import io.quarkiverse.morphia.MorphiaConfig;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class MorphiaProcessor {
    private static final String FEATURE = "morphia";
    private static final Class<?> PROVIDER_CLASS = DatastoreProvider.class;

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    public SyntheticBeanBuildItem createDatastore(MorphiaRecorder recorder, MorphiaConfig config) {
        return SyntheticBeanBuildItem
                .configure(Datastore.class)
                .scope(Singleton.class)
                .supplier(recorder.configureMapperOptions(config))
                .setRuntimeInit()
                .done();
    }

    @BuildStep
    public DatastoreProviderBuildItem createDatastore(
            CombinedIndexBuildItem index,
            MorphiaConfig config,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {

        AdditionalBeanBuildItem.Builder builder = AdditionalBeanBuildItem.builder()
                .setUnremovable()
                .addBeanClass(DatastoreProvider.class);

        additionalBeans.produce(builder.build());

        return new DatastoreProviderBuildItem(PROVIDER_CLASS);
    }

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
}
