package io.quarkiverse.morphia;

import static dev.morphia.mapping.NamingStrategy.camelCase;
import static dev.morphia.mapping.NamingStrategy.identity;
import static dev.morphia.mapping.NamingStrategy.kebabCase;
import static dev.morphia.mapping.NamingStrategy.lowerCase;
import static dev.morphia.mapping.NamingStrategy.snakeCase;

public enum NamingStrategy {
    identity {
        @Override
        dev.morphia.mapping.NamingStrategy convert() {
            return identity();
        }
    },
    lowerCase {
        @Override
        dev.morphia.mapping.NamingStrategy convert() {
            return lowerCase();
        }
    },
    snakeCase {
        @Override
        dev.morphia.mapping.NamingStrategy convert() {
            return snakeCase();
        }
    },
    camelCase {
        @Override
        dev.morphia.mapping.NamingStrategy convert() {
            return camelCase();
        }
    },
    kebabCase {
        @Override
        dev.morphia.mapping.NamingStrategy convert() {
            return kebabCase();
        }
    };

    abstract dev.morphia.mapping.NamingStrategy convert();
}
