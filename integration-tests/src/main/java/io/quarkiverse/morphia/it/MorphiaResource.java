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
import static java.util.List.of;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import dev.morphia.Datastore;
import io.quarkiverse.morphia.it.models.Book;

@Path("/morphia")
@ApplicationScoped
public class MorphiaResource {
    @Inject
    Datastore datastore;

    @GET
    @Produces("application/text")
    public String databaseName() {
        return datastore.getDatabase().getName();
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
}
