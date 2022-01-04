package io.quarkiverse.morphia.deployment;

import io.quarkus.builder.item.MultiBuildItem;

public final class DatastoreProviderBuildItem extends MultiBuildItem {
    private final Class<?> klass;

    public DatastoreProviderBuildItem(Class<?> klass) {
        this.klass = klass;
    }

    public Class<?> getProviderClass() {
        return klass;
    }

    @Override
    public String toString() {
        return "DatastoreProviderBuildItem{" +
                "klass=" + klass +
                '}';
    }
}
