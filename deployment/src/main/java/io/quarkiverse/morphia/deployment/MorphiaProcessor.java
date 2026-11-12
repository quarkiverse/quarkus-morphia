package io.quarkiverse.morphia.deployment;

import static java.util.List.of;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;

import org.bson.codecs.Codec;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Indexer;
import org.jboss.jandex.JarIndexer;

import dev.morphia.Datastore;
import dev.morphia.annotations.AlsoLoad;
import dev.morphia.annotations.CappedAt;
import dev.morphia.annotations.Collation;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.EntityListeners;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Handler;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IdGetter;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.Indexes;
import dev.morphia.annotations.LoadOnly;
import dev.morphia.annotations.NotSaved;
import dev.morphia.annotations.PostLoad;
import dev.morphia.annotations.PostPersist;
import dev.morphia.annotations.PreLoad;
import dev.morphia.annotations.PrePersist;
import dev.morphia.annotations.Property;
import dev.morphia.annotations.Reference;
import dev.morphia.annotations.Text;
import dev.morphia.annotations.Transient;
import dev.morphia.annotations.Validation;
import dev.morphia.annotations.Version;
import dev.morphia.mapping.codec.references.ReferenceCodec;
import io.quarkiverse.morphia.MorphiaRecorder;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;
import io.quarkus.mongodb.MongoClientName;
import io.quarkus.mongodb.deployment.spi.MongoClientBuildItem;
import io.quarkus.mongodb.deployment.spi.MongoClientsBuildItem;
import io.quarkus.mongodb.runtime.MongoClientRecorder;
import io.smallrye.common.constraint.NotNull;

@SuppressWarnings("removal")
public class MorphiaProcessor {
    private static final List<DotName> MAPPED_TYPE_ANNOTATIONS = of(DotName.createSimple(Entity.class.getName()),
            DotName.createSimple(Embedded.class.getName()));
    private static final String FEATURE = "quarkus-morphia";
    public static final DotName MONGO_CLIENT_NAME = DotName.createSimple(MongoClientName.class.getName());
    private static final String ENTITY_MODEL_IMPORTER = "dev.morphia.mapping.EntityModelImporter";

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    public void datastoreRecorder(MongoClientRecorder clientRecorder,
            MorphiaRecorder recorder,
            MorphiaEntitiesBuildItem entitiesBuildItem,
            MongoClientsBuildItem mongoClients,
            BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer,
            BuildProducer<ServiceProviderBuildItem> items) {

        // the default client and the clients referenced with @MongoClientName
        for (MongoClientBuildItem mongoClient : mongoClients.getMongoClients()) {

            String clientName = mongoClient.getName();

            SyntheticBeanBuildItem.ExtendedBeanConfigurator configurator = SyntheticBeanBuildItem
                    .configure(Datastore.class)
                    .scope(ApplicationScoped.class)
                    .supplier(recorder.datastoreSupplier(
                            clientRecorder.mongoClientSupplier(clientName),
                            entitiesBuildItem.getNames(), clientName))
                    .setRuntimeInit();

            if (mongoClient.isDefault()) {
                configurator.addQualifier(Default.class);
            } else {
                configurator.addQualifier().annotation(DotNames.NAMED).addValue("value", clientName).done();
                configurator.addQualifier().annotation(MONGO_CLIENT_NAME).addValue("value", clientName).done();
            }

            syntheticBeanBuildItemBuildProducer.produce(configurator.done());
        }
        items.produce(ServiceProviderBuildItem.allProvidersFromClassPath(ENTITY_MODEL_IMPORTER));
    }

    @BuildStep
    public void registerReflectiveItems(BuildProducer<ReflectiveClassBuildItem> reflectiveClasses,
            BuildProducer<GeneratedResourceBuildItem> resourceItems) throws IOException {
        registerWrapperArrays(reflectiveClasses);
        Index index = indexJar();
        registerImplementors(index, reflectiveClasses, DotName.createSimple(Codec.class.getName()));
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(ReferenceCodec.class).methods().build());
        registerAnnotations(reflectiveClasses);
    }

    private void registerAnnotations(BuildProducer<ReflectiveClassBuildItem> reflectiveClasses) {
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(AlsoLoad.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(CappedAt.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Collation.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Converters.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Embedded.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Entity.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(EntityListeners.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Field.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Handler.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Id.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(IdGetter.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Indexed.class).methods().fields().build());
        reflectiveClasses
                .produce(ReflectiveClassBuildItem.builder(dev.morphia.annotations.Index.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Indexes.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(IndexOptions.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(LoadOnly.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(NotSaved.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(PostLoad.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(PostPersist.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(PreLoad.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(PrePersist.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Property.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Reference.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Text.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Transient.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Validation.class).methods().fields().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Version.class).methods().fields().build());
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

    @BuildStep
    public MorphiaEntitiesBuildItem registerMappedTypes(CombinedIndexBuildItem indexBuildItem,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClasses) {
        ArrayList<String> list = new ArrayList<>();

        IndexView index = indexBuildItem.getIndex();
        for (DotName annotation : MAPPED_TYPE_ANNOTATIONS) {
            var classes = index.getKnownClasses().stream()
                    .filter(info -> info.classAnnotation(annotation) != null)
                    .collect(Collectors.toList());
            classes.forEach(info -> {
                reflectiveClasses.produce(ReflectiveClassBuildItem.builder(info.name().toString()).methods().fields().build());
                list.add(info.name().toString());
            });
        }

        return new MorphiaEntitiesBuildItem(list);
    }

    private void registerSubclasses(Index index, BuildProducer<ReflectiveClassBuildItem> reflectiveClasses, DotName type) {
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(type.toString()).methods().build());
        index.getAllKnownSubclasses(type).forEach(codec -> registerSubclasses(index, reflectiveClasses, type));
    }

    private void registerWrapperArrays(BuildProducer<ReflectiveClassBuildItem> reflectiveClasses) {
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Boolean[].class).methods().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Byte[].class).methods().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Character[].class).methods().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Double[].class).methods().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Float[].class).methods().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Integer[].class).methods().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(Long[].class).methods().build());
    }

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
}
