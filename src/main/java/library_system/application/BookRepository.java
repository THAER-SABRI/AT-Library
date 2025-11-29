package library_system.application;

import java.util.List;
import library_system.domain.Book;

public interface BookRepository {
    List<Book> getAll();
    void saveAll(List<Book> books);
}
