package io.quarkiverse.morphia.deployment;

import dev.morphia.Datastore;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.experimental.EmbeddedBuilder;
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
import org.bson.codecs.Codec;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;
import org.jboss.jandex.Indexer;
import org.jboss.jandex.JarIndexer;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import static io.quarkus.mongodb.runtime.MongoClientBeanUtil.DEFAULT_MONGOCLIENT_NAME;
import static java.util.List.of;

public class MorphiaProcessor {
    public static final List<DotName> MAPPED_TYPE_ANNOTATIONS = of(DotName.createSimple(Entity.class.getName()),
            DotName.createSimple(EmbeddedBuilder.class.getName()));
    private static final String FEATURE = "morphia";

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    public SyntheticBeanBuildItem datastoreRecorder(MongoClientRecorder clientRecorder,
            MongodbConfig mongodbConfig,
            MorphiaRecorder recorder,
            MorphiaConfig config) {

        return SyntheticBeanBuildItem.configure(Datastore.class)
                .scope(Singleton.class)
                .supplier(recorder.datastoreSupplier(
                        clientRecorder.mongoClientSupplier(DEFAULT_MONGOCLIENT_NAME, mongodbConfig), config))
                .setRuntimeInit()
                .done();
    }

    @BuildStep
    public void registerReflectiveItems(BuildProducer<ReflectiveClassBuildItem> reflectiveClasses) throws IOException {
        registerWrapperArrays(reflectiveClasses);
        Index index = indexJar();
        registerImplementors(index, reflectiveClasses, DotName.createSimple(Codec.class.getName()));
        //        registerMappedTypes(index, reflectiveClasses);
        reflectiveClasses.produce(new ReflectiveClassBuildItem(true, false, ReferenceCodec.class));
    }

    @NotNull
    private Index indexJar() throws IOException {
        URL location = Datastore.class.getProtectionDomain().getCodeSource().getLocation();
        Indexer indexer = new Indexer();
        JarIndexer.createJarIndex(new File(location.getFile()), indexer, false, false, false);
        return indexer.complete();
    }

    private void registerImplementors(Index index, BuildProducer<ReflectiveClassBuildItem> reflectiveClasses, DotName type) {
        Set<ClassInfo> set = index.getAllKnownImplementors(type);
        set.forEach(codec -> registerImplementors(index, reflectiveClasses, codec.name()));
        registerSubclasses(index, reflectiveClasses, type);
    }

    private void registerMappedTypes(Index index, BuildProducer<ReflectiveClassBuildItem> reflectiveClasses) {
        for (DotName annotation : MAPPED_TYPE_ANNOTATIONS) {
            index.getKnownClasses().stream().filter(info -> info.classAnnotation(annotation) != null).forEach(info -> {
                reflectiveClasses.produce(new ReflectiveClassBuildItem(true, true, info.name().toString()));
            });
        }
    }

    private void registerSubclasses(Index index, BuildProducer<ReflectiveClassBuildItem> reflectiveClasses, DotName type) {
        reflectiveClasses.produce(new ReflectiveClassBuildItem(true, false, type.toString()));
        index.getAllKnownSubclasses(type).forEach(codec -> registerSubclasses(index, reflectiveClasses, type));
    }

    private void registerWrapperArrays(BuildProducer<ReflectiveClassBuildItem> reflectiveClasses) {
        reflectiveClasses.produce(new ReflectiveClassBuildItem(true, false, Boolean[].class));
        reflectiveClasses.produce(new ReflectiveClassBuildItem(true, false, Byte[].class));
        reflectiveClasses.produce(new ReflectiveClassBuildItem(true, false, Character[].class));
        reflectiveClasses.produce(new ReflectiveClassBuildItem(true, false, Double[].class));
        reflectiveClasses.produce(new ReflectiveClassBuildItem(true, false, Float[].class));
        reflectiveClasses.produce(new ReflectiveClassBuildItem(true, false, Integer[].class));
        reflectiveClasses.produce(new ReflectiveClassBuildItem(true, false, Long[].class));
    }

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
}
