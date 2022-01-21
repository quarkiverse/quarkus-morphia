package io.quarkiverse.morphia.test;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import dev.morphia.Datastore;
import io.quarkus.mongodb.MongoClientName;
import io.quarkus.test.QuarkusUnitTest;

public class DatastoreTest {
    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withConfigurationResource("test-logging.properties")
            .overrideConfigKey("quarkus.morphia.database", "quarkus-morphia-test")
            .overrideConfigKey("quarkus.morphia.alternate.database", "quarkus-alternate-test")
            .overrideConfigKey("quarkus.mongodb.alternate.connection-string", "mongodb://127.0.0.1:27017")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @Inject
    Datastore datastore;

    @Inject
    @MongoClientName("alternate")
    Datastore alternate;

    @Test
    public void testDatastorePresent() {
        Assertions.assertNotNull(datastore, "A datastore should be configured");
        Assertions.assertEquals("quarkus-morphia-test", datastore.getDatabase().getName(),
                "A datastore should be configured");
    }

    @Test
    public void namedDatastores() {
        Assertions.assertEquals("quarkus-morphia-test", datastore.getDatabase().getName(),
                "A datastore should be configured");
        Assertions.assertEquals("quarkus-alternate-test", alternate.getDatabase().getName(),
                "An alternate datastore should be configured");
    }
}
