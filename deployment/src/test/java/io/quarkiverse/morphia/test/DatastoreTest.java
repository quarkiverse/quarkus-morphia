package io.quarkiverse.morphia.test;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import dev.morphia.Datastore;
import io.quarkus.test.QuarkusUnitTest;

public class DatastoreTest {
    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withConfigurationResource("test-logging.properties")
            .overrideConfigKey("quarkus.morphia.database", "quarkus-morphia-test")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @Inject
    Datastore datastore;

    @Test
    public void testDatastorePresent() {
        Assertions.assertNotNull(datastore, "A datastore should be configured");
    }
}
