package io.quarkiverse.morphia.runtime;

import static dev.morphia.mapping.DiscriminatorFunction.className;
import static dev.morphia.mapping.DiscriminatorFunction.lowerClassName;
import static dev.morphia.mapping.DiscriminatorFunction.lowerSimpleName;
import static dev.morphia.mapping.DiscriminatorFunction.simpleName;

import dev.morphia.mapping.DiscriminatorFunction;

public enum Discriminator {
    className {
        @Override
        DiscriminatorFunction convert() {
            return className();
        }

    },
    lowerClassName {
        @Override
        DiscriminatorFunction convert() {
            return lowerClassName();
        }

    },
    lowerSimpleName {
        @Override
        DiscriminatorFunction convert() {
            return lowerSimpleName();
        }

    },
    simpleName {
        @Override
        DiscriminatorFunction convert() {
            return simpleName();
        }

    };

    abstract DiscriminatorFunction convert();
}
