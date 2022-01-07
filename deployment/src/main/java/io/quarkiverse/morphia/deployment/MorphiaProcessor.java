package io.quarkiverse.morphia.deployment;

import static io.quarkus.mongodb.runtime.MongoClientBeanUtil.DEFAULT_MONGOCLIENT_NAME;

import javax.inject.Singleton;

import dev.morphia.Datastore;
import io.quarkiverse.morphia.MorphiaConfig;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.mongodb.runtime.MongoClientRecorder;
import io.quarkus.mongodb.runtime.MongodbConfig;

public class MorphiaProcessor {
    private static final String FEATURE = "morphia";

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    public SyntheticBeanBuildItem datastoreRecorder(MongoClientRecorder clientRecorder, MongodbConfig mongodbConfig,
            MorphiaRecorder recorder, MorphiaConfig config) {
        return SyntheticBeanBuildItem
                .configure(Datastore.class)
                .scope(Singleton.class)
                .supplier(recorder
                        .datastoreSupplier(clientRecorder.mongoClientSupplier(DEFAULT_MONGOCLIENT_NAME, mongodbConfig), config))
                .setRuntimeInit()
                .done();
    }

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
}
