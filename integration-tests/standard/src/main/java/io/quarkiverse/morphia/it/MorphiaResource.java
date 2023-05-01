/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.quarkiverse.morphia.it;

import static dev.morphia.query.filters.Filters.eq;
import static java.lang.Boolean.TRUE;
import static java.util.List.of;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import org.bson.Document;

import dev.morphia.Datastore;
import dev.morphia.aggregation.stages.Lookup;
import dev.morphia.aggregation.stages.Unwind;
import dev.morphia.mapping.codec.pojo.EntityModel;
import io.quarkiverse.morphia.it.models.Author;
import io.quarkiverse.morphia.it.models.Book;
import io.quarkus.mongodb.MongoClientName;

@Path("/morphia")
@ApplicationScoped
public class MorphiaResource {
    @Inject
    @Default
    Datastore datastore;

    @Inject
    @MongoClientName("alternate")
    Datastore alternate;

    @Inject
    @MongoClientName("critter")
    Datastore critter;

    @GET
    @Path("alternates")
    @Produces("application/text")
    public Response alternates() {
        return Response.ok(
                datastore != alternate &&
                        datastore.getDatabase().getName().equals("morphia-int-test") &&
                        alternate.getDatabase().getName().equals("morphia-alternate") &&
                        critter.getDatabase().getName().equals("morphia-critter"))
                .build();
    }

    @POST
    @Consumes("application/json")
    @Produces("application/text")
    public Response addBook(Book book) {
        datastore.save(book.author);
        datastore.save(book);
        return Response.accepted(book.id.toString()).build();
    }

    @GET
    @Path("/caps")
    @Produces("application/text")
    public Response cappedCollections() {
        Document options = getOptions("authors");
        Boolean capped = options.getBoolean("capped");
        Integer max = options.getInteger("max");
        return Response.ok(TRUE.equals(capped) && max.equals(100)).build();
    }

    @GET
    @Path("/mapping")
    @Produces("application/text")
    public Response mapping() {
        var list = datastore.getMapper().getMappedEntities().stream()
                .map(EntityModel::getName).sorted()
                .collect(Collectors.joining(", "));

        return Response.ok(list).build();
    }

    @GET
    @Path("/alternateMapping")
    @Produces("application/text")
    public Response alternateMapping() {
        var list = alternate.getMapper().getMappedEntities().stream()
                .map(EntityModel::getName).sorted()
                .collect(Collectors.joining(", "));

        return Response.ok(list).build();
    }

    @GET
    @Path("/critterMapping")
    @Produces("application/text")
    public Response critterMapping() {
        var list = critter.getMapper().getMappedEntities().stream()
                .map(EntityModel::getName).sorted()
                .collect(Collectors.joining(", "));

        return Response.ok(list).build();
    }

    @GET
    @Produces("application/text")
    public String databaseName() {
        return datastore.getDatabase().getName();
    }

    @GET
    @Path("/validation")
    @Produces("application/text")
    public Response documentValidation() {
        datastore.enableDocumentValidation();

        Document options = getOptions("books");
        Document validator = (Document) options.get("validator");
        return Response.ok(validator != null && validator.get("title") != null).build();

    }

    @GET
    @Path("/all")
    @Produces("application/json")
    public List<Book> getBooks() {
        return datastore.find(Book.class).iterator().toList();
    }

    @GET
    @Path("/{author}")
    @Produces("application/json")
    public List<Book> getBooksByAuthor(@PathParam("author") String author) {
        return datastore.aggregate(Book.class)
                .lookup(Lookup.lookup(Author.class)
                        .localField("author")
                        .foreignField("_id")
                        .as("author"))
                .unwind(Unwind.unwind("author"))
                .match(eq("author.name", author))
                .execute(Book.class)
                .toList();
    }

    @GET
    @Path("/index")
    @Produces("application/text")
    public Response index() {
        datastore.ensureIndexes();

        return Response.ok(datastore.getCollection(Book.class)
                .listIndexes()
                .into(new ArrayList<>()).stream()
                .map(index -> index.get("name").toString())
                .collect(Collectors.joining(", ")))
                .build();
    }

    @GET
    @Path("/create")
    @Produces("application/json")
    public Book persistAndReturn() {
        Book book = new Book();
        book.title = "The Eye of the World";
        datastore.save(of(book));
        return datastore.find(Book.class).filter(eq("_id", book.id)).first();
    }

    @SuppressWarnings("unchecked")
    private Document getOptions(String collection) {
        Document result = datastore.getDatabase().runCommand(
                new Document("listCollections", 1)
                        .append("filter", new Document("name", collection)));
        Document cursor = (Document) result.get("cursor");
        List<Document> firstBatch = (List<Document>) cursor.get("firstBatch");
        Document document = firstBatch.get(0);

        return (Document) document.get("options");
    }

}
