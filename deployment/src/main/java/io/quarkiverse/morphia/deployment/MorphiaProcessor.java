package io.quarkiverse.morphia.deployment;

import static io.quarkus.mongodb.runtime.MongoClientBeanUtil.DEFAULT_MONGOCLIENT_NAME;

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
import io.quarkus.mongodb.runtime.MongoClientRecorder;
import io.quarkus.mongodb.runtime.MongodbConfig;

public class MorphiaProcessor {
    private static final String FEATURE = "morphia";
    private static final Class<?> PROVIDER_CLASS = DatastoreProvider.class;

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    public SyntheticBeanBuildItem datastoreRecorder(MongoClientRecorder clientRecorder, MongodbConfig mongodbConfig,
            MorphiaRecorder recorder,
            MorphiaConfig config) {
        System.out.println("**********8 MorphiaProcessor.datastoreRecorder");
        return SyntheticBeanBuildItem
                .configure(Datastore.class)
                .scope(Singleton.class)
                .supplier(recorder
                        .datastoreSupplier(clientRecorder.mongoClientSupplier(DEFAULT_MONGOCLIENT_NAME, mongodbConfig), config))
                .setRuntimeInit()
                .done();
    }

    //    @BuildStep
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
