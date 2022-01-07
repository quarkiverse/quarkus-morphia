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
        /*
         * @GET
         * 
         * @Path("/create")
         * 
         * @Produces("application/json")
         * public Book persistAndReturn() {
         * Book book = new Book();
         * book.title = "The Eye of the World";
         * datastore.save(of(book));
         * return datastore.find(Book.class).filter(eq("_id", book.id)).first();
         * }
         */

        given()
                .when().get("/morphia/create")
                .then()
                .statusCode(200)
                .body(isA(String.class))
                .body(containsString("The Eye of the World"));
    }
}
