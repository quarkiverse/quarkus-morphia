package io.quarkiverse.morphia.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MorphiaResourceTest {

    @Test()
    public void testDatastoreConfig() {
        given()
                .when().get("/morphia")
                .then()
                .statusCode(200)
                .body(is("morphia-int-test"));
    }

    @Test()
    public void testPersist() {
        given()
                .when().get("/morphia/create")
                .then()
                .statusCode(200)
                .body(isA(String.class))
                .body(containsString("The Eye of the World"));
    }
}
