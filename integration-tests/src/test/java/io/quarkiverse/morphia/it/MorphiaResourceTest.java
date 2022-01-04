package io.quarkiverse.morphia.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MorphiaResourceTest {

    @Test
    public void testDatastoreConfig() {
        given()
                .when().get("/morphia")
                .then()
                .statusCode(200)
                .body(is("morphia-int-test"));
    }
}
