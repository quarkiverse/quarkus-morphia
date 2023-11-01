package io.quarkiverse.morphia.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;

import org.bson.types.ObjectId;
import org.hamcrest.Description;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;

import io.quarkiverse.morphia.it.models.Author;
import io.quarkiverse.morphia.it.models.Book;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MorphiaResourceTest {
    static final MongoDBContainer CONTAINER;

    static {
        CONTAINER = new MongoDBContainer("mongo:7");
        CONTAINER.start();
    }

    @Test
    public void testAlternates() {
        given()
                .when().get("/morphia/alternates")
                .then()
                .statusCode(200)
                .body(is("true"));
    }

    @Test
    public void testCaps() {
        given()
                .when().get("/morphia/caps")
                .then()
                .statusCode(200)
                .body(is("true"));
    }

    @Test()
    public void testDatastoreConfig() {
        given()
                .when().get("/morphia")
                .then()
                .statusCode(200)
                .body(is("morphia-int-test"));
    }

    @Test()
    public void testIndexing() {
        given()
                .when().get("/morphia/index")
                .then()
                .statusCode(200)
                .body(is("_id_, title_text, published_1"));
    }

    @Test
    public void testMapping() {
        given()
                .when().get("/morphia/mapping")
                .then()
                .statusCode(200)
                .body(is("Author, Book"));
    }

    @Test
    public void testAlternateMapping() {
        given()
                .when().get("/morphia/alternateMapping")
                .then()
                .statusCode(200)
                .body(is("Author, Book, Car, Moto, Vehicle"));
    }

    @Test
    public void testCritterMapping() {
        given()
                .when().get("/morphia/critterMapping")
                .then()
                .statusCode(200)
                .body(is("Author, Book, Car, Moto, Vehicle"));
    }

    @Test()
    public void testPersist() {
        Book book = new Book("The Eye of the World");
        book.author = new Author("Robert Jordan");

        given()
                .header("Content-Type", "application/json")
                .body(book)
                .when().post("/morphia/")
                .then()
                .statusCode(202)
                .body(isA(String.class))
                .body(new IsInstanceOf(ObjectId.class) {
                    @Override
                    protected boolean matches(Object item, Description mismatch) {
                        return ObjectId.isValid(item.toString());
                    }
                });

        given()
                .header("Content-Type", "application/json")
                .when().get("/morphia/Robert Jordan")
                .then()
                .statusCode(200)
                .body(containsString("Robert Jordan"));

    }

    @Test()
    public void testValidation() {
        given()
                .when().get("/morphia/validation")
                .then()
                .statusCode(200)
                .body(is("true"));
    }
}
