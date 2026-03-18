package io.quarkiverse.morphia.it;

import java.util.HashMap;
import java.util.Map;

import org.testcontainers.containers.MongoDBContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class MongoTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    static MongoDBContainer mongoDBContainer;

    @Override
    public Map<String, String> start() {
        mongoDBContainer = new MongoDBContainer("mongo:7");
        mongoDBContainer.start();
        String uri = mongoDBContainer.getConnectionString();
        Map<String, String> config = new HashMap<>();
        config.put("quarkus.mongodb.connection-string", uri);
        config.put("quarkus.mongodb.alternate.connection-string", uri);
        config.put("quarkus.mongodb.critter.connection-string", uri);
        return config;
    }

    @Override
    public void stop() {
        if (mongoDBContainer != null) {
            mongoDBContainer.stop();
        }
    }
}
