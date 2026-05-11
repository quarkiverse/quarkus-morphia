package io.quarkiverse.morphia.deployment;

import static io.quarkus.mongodb.runtime.MongoClientBeanUtil.DEFAULT_MONGOCLIENT_NAME;
import static java.util.List.of;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
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
import io.quarkiverse.morphia.QuarkusMorphiaConfig;
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
import io.quarkus.mongodb.deployment.MongoClientNameBuildItem;
import io.quarkus.mongodb.runtime.MongoClientBeanUtil;
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
            QuarkusMorphiaConfig config,
            MorphiaEntitiesBuildItem entitiesBuildItem,
            List<MongoClientNameBuildItem> mongoClientNames,
            BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer,
            BuildProducer<ServiceProviderBuildItem> items) {

        syntheticBeanBuildItemBuildProducer.produce(SyntheticBeanBuildItem
                .configure(Datastore.class)
                .scope(ApplicationScoped.class)
                .supplier(recorder.datastoreSupplier(
                        clientRecorder.mongoClientSupplier(DEFAULT_MONGOCLIENT_NAME), config,
                        entitiesBuildItem.getNames(), DEFAULT_MONGOCLIENT_NAME))
                .setRuntimeInit()
                .done());

        for (MongoClientNameBuildItem mongoClientName : mongoClientNames) {

            String clientName = mongoClientName.getName();

            SyntheticBeanBuildItem.ExtendedBeanConfigurator configurator = SyntheticBeanBuildItem
                    .configure(Datastore.class)
                    .scope(ApplicationScoped.class)
                    .supplier(recorder.datastoreSupplier(
                            clientRecorder.mongoClientSupplier(clientName), config,
                            entitiesBuildItem.getNames(), clientName))
                    .setRuntimeInit();

            if (MongoClientBeanUtil.isDefault(clientName)) {
                configurator.addQualifier(Default.class);
            } else {
                configurator.addQualifier().annotation(DotNames.NAMED).addValue("value", clientName).done();
                if (mongoClientName.isAddQualifier()) {
                    configurator.addQualifier().annotation(MONGO_CLIENT_NAME).addValue("value", clientName).done();
                }
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
        final List<String> forReflection = new ArrayList<>();
        registerImplementors(index, forReflection, DotName.createSimple(Codec.class.getName()));
        reflectiveClasses.produce(
                ReflectiveClassBuildItem.builder(forReflection.toArray(new String[0])).constructors().methods().build());
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(ReferenceCodec.class).constructors().methods().build());
        registerAnnotations(reflectiveClasses);
    }

    private void registerAnnotations(BuildProducer<ReflectiveClassBuildItem> reflectiveClasses) {
        final String[] forReflection = new String[] {
                AlsoLoad.class.getName(),
                CappedAt.class.getName(),
                Collation.class.getName(),
                Converters.class.getName(),
                Embedded.class.getName(),
                Entity.class.getName(),
                EntityListeners.class.getName(),
                Field.class.getName(),
                Handler.class.getName(),
                Id.class.getName(),
                IdGetter.class.getName(),
                Indexed.class.getName(),
                dev.morphia.annotations.Index.class.getName(),
                Indexes.class.getName(),
                IndexOptions.class.getName(),
                LoadOnly.class.getName(),
                NotSaved.class.getName(),
                PostLoad.class.getName(),
                PostPersist.class.getName(),
                PreLoad.class.getName(),
                PrePersist.class.getName(),
                Property.class.getName(),
                Reference.class.getName(),
                Text.class.getName(),
                Transient.class.getName(),
                Validation.class.getName(),
                Version.class.getName()
        };
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(forReflection).constructors().methods().fields().build());
    }

    @NotNull
    private Index indexJar() throws IOException {
        URL location = Datastore.class.getProtectionDomain().getCodeSource().getLocation();
        Indexer indexer = new Indexer();
        JarIndexer.createJarIndex(new File(location.getFile()), indexer, false, false, false);
        return indexer.complete();
    }

    private void registerImplementors(Index index, List<String> forReflection, DotName type) {
        Set<ClassInfo> set = index.getAllKnownImplementors(type);
        set.forEach(codec -> registerImplementors(index, forReflection, codec.name()));
        registerSubclasses(index, forReflection, type);
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
            classes.forEach(info -> list.add(info.name().toString()));
        }

        reflectiveClasses.produce(
                ReflectiveClassBuildItem.builder(list.toArray(new String[0])).constructors().methods().fields().build());
        return new MorphiaEntitiesBuildItem(list);
    }

    private void registerSubclasses(Index index, List<String> forReflection, DotName type) {
        // Wrapper that initializes the visited set to avoid infinite recursion and duplicates
        registerSubclasses(index, forReflection, type, new HashSet<>());
    }

    private void registerSubclasses(Index index, List<String> forReflection, DotName type, Set<DotName> visited) {
        // If we've already processed this type, avoid re-processing to prevent cycles/duplicates
        if (!visited.add(type)) {
            return;
        }
        forReflection.add(type.toString());
        index.getAllKnownSubclasses(type)
                .forEach(codec -> registerSubclasses(index, forReflection, codec.name(), visited));
    }

    private void registerWrapperArrays(BuildProducer<ReflectiveClassBuildItem> reflectiveClasses) {
        final String[] forReflection = new String[] {
                Boolean[].class.getName(),
                Byte[].class.getName(),
                Character[].class.getName(),
                Double[].class.getName(),
                Float[].class.getName(),
                Integer[].class.getName(),
                Long[].class.getName() };
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(forReflection).constructors().methods().build());
    }

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
}
