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

import static dev.morphia.query.experimental.filters.Filters.eq;
import static java.lang.Boolean.TRUE;
import static java.util.List.of;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.bson.Document;

import dev.morphia.Datastore;
import dev.morphia.aggregation.experimental.stages.Lookup;
import dev.morphia.aggregation.experimental.stages.Unwind;
import io.quarkiverse.morphia.it.models.Author;
import io.quarkiverse.morphia.it.models.Book;

@Path("/morphia")
@ApplicationScoped
public class MorphiaResource {
    @Inject
    Datastore datastore;

    @POST
    @Consumes("application/json")
    public Response addBook(Book book) {
        datastore.save(book.author);
        datastore.save(book);
        return Response.accepted(book.id.toString()).build();
    }

    @GET
    @Path("/caps")
    public Response cappedCollections() {
        print("*************** collections = " + datastore.getDatabase().listCollectionNames().into(new ArrayList<>()));
        datastore.getDatabase().drop();
        print("*************** collections = " + datastore.getDatabase().listCollectionNames().into(new ArrayList<>()));
        datastore.getMapper().mapPackageFromClass(Book.class);
        print("*************** entities = " + datastore.getMapper().getMappedEntities());
        print("*************** collections = " + datastore.getDatabase().listCollectionNames().into(new ArrayList<>()));
        //        datastore.enableDocumentValidation();
        //        System.out.println(
        //                "*************** collections = " + datastore.getDatabase().listCollectionNames().into(new ArrayList<>()));
        datastore.ensureCaps();
        print("*************** collections = " + datastore.getDatabase().listCollectionNames().into(new ArrayList<>()));
        print("*************** entities = " + datastore.getMapper().getMappedEntities());

        Document options = getOptions();
        Boolean capped = options.getBoolean("capped");
        return Response.ok(TRUE.equals(capped) && options.get("max").equals(100)).build();
    }

    private void print(String datastore) {
        System.out.println(
                datastore);
    }

    @GET
    @Produces("application/text")
    public String databaseName() {
        return datastore.getDatabase().getName();
    }

    @GET
    @Path("/validation")
    public Response documentValidation() {
        datastore.enableDocumentValidation();

        Document options = getOptions();
        Document validator = (Document) options.get("validator");
        return Response.ok(validator != null && validator.get("name") != null).build();

    }

    @GET
    @Path("/all")
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
    public Response index() {
        datastore.ensureIndexes();

        return Response.ok(datastore.getMapper().getCollection(Book.class)
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
    private Document getOptions() {
        Document result = datastore.getDatabase().runCommand(
                new Document("listCollections", 1)
                        .append("filter", new Document("name", "books")));
        Document cursor = (Document) result.get("cursor");
        print("---- cursor = " + cursor);
        List<Document> firstBatch = (List<Document>) cursor.get("firstBatch");
        print("---- firstBatch = " + firstBatch);
        Document document = firstBatch.get(0);

        Document options = (Document) document.get("options");
        return options;
    }

}
