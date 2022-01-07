package io.quarkiverse.morphia.it.models;

import java.time.LocalDate;
import java.util.List;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;

@Entity
public class Book {
    @Id
    public ObjectId id;
    public String title;
    public LocalDate published;
    @Reference(lazy = true)
    public List<Author> authors;
}
