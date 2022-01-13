package io.quarkiverse.morphia.deployment;

import java.util.List;

import io.quarkus.builder.item.SimpleBuildItem;

public final class CodecBuildItem extends SimpleBuildItem {

    private final List<String> names;

    public CodecBuildItem(List<String> names) {
        this.names = names;
    }

    public List<String> getNames() {
        return names;
    }
}
