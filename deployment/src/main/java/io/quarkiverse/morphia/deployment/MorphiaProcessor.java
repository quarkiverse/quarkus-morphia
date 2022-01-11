package io.quarkiverse.morphia.deployment;

import static io.quarkus.mongodb.runtime.MongoClientBeanUtil.DEFAULT_MONGOCLIENT_NAME;

import javax.inject.Singleton;

import dev.morphia.Datastore;
import dev.morphia.mapping.codec.references.ReferenceCodec;
import io.quarkiverse.morphia.MorphiaConfig;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
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
    public void registerReflectiveItems(BuildProducer<ReflectiveClassBuildItem> reflectiveClasses) {
        reflectiveClasses.produce(new ReflectiveClassBuildItem(true, false, Boolean[].class));
        reflectiveClasses.produce(new ReflectiveClassBuildItem(true, false, Byte[].class));
        reflectiveClasses.produce(new ReflectiveClassBuildItem(true, false, Character[].class));
        reflectiveClasses.produce(new ReflectiveClassBuildItem(true, false, Double[].class));
        reflectiveClasses.produce(new ReflectiveClassBuildItem(true, false, Float[].class));
        reflectiveClasses.produce(new ReflectiveClassBuildItem(true, false, Integer[].class));
        reflectiveClasses.produce(new ReflectiveClassBuildItem(true, false, Long[].class));
        reflectiveClasses.produce(new ReflectiveClassBuildItem(true, false, ReferenceCodec.class));
    }

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
}
