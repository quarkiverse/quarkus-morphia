package io.quarkiverse.morphia.it.models;

import java.time.LocalDate;
import java.util.Objects;

import org.bson.types.ObjectId;

import dev.morphia.annotations.CappedAt;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.Indexes;
import dev.morphia.annotations.Reference;
import dev.morphia.annotations.Validation;
import dev.morphia.utils.IndexType;

@Entity(value = "books", cap = @CappedAt(count = 100))
@Validation("{ name: { $exists:  true } }")
@Indexes(value = @Index(fields = @Field(value = "title", type = IndexType.TEXT)))
public class Book {
    @Id
    public ObjectId id;
    public String title;
    @Indexed
    public LocalDate published;
    @Reference(idOnly = true)
    public Author author;

    public Book() {
    }

    public Book(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        Book book = (Book) o;
        return Objects.equals(id, book.id) && Objects.equals(title, book.title) &&
                Objects.equals(published, book.published) && Objects.equals(author, book.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, published, author);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", published=" + published +
                ", author=" + author +
                '}';
    }
}
