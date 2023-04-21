package io.quarkiverse.morphia.it.mongodb;

import static dev.morphia.query.filters.Filters.eq;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

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
