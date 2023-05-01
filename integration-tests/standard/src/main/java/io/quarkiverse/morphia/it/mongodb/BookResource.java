package io.quarkiverse.morphia.it.mongodb;

import static dev.morphia.query.filters.Filters.eq;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import dev.morphia.Datastore;
import io.quarkiverse.morphia.it.models.Book;
import io.smallrye.common.annotation.Blocking;

@Path("/books")
@Blocking
public class BookResource {

    @Inject
    Datastore datastore;

    @GET
    public List<Book> getBooks() {
        return datastore.find(Book.class).iterator().toList();
    }

    @POST
    public Response addBook(Book book) {
        datastore.save(book);
        return Response.accepted().build();
    }

    @GET
    @Path("/{author}")
    public List<Book> getBooksByAuthor(@PathParam("author") String author) {
        return datastore.find(Book.class)
                .filter(eq("author", author))
                .iterator()
                .toList();
    }

}
