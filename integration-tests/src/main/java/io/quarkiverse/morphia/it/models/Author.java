package io.quarkiverse.morphia.it.models;

import java.util.List;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;

@Entity
public class Author {
    @Id
    public ObjectId id;
    public String name;
    @Reference(lazy = true)
    public List<Book> books;
}
