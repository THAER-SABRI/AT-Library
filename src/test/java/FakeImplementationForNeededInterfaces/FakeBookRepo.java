package FakeImplementationForNeededInterfaces;

import library_system.application.BookRepository;
import library_system.domain.Book;

import java.util.ArrayList;
import java.util.List;

public class FakeBookRepo implements BookRepository {

    public List<Book> books = new ArrayList<>();

    @Override
    public List<Book> getAll() {
        return books;
    }

    @Override
    public void saveAll(List<Book> books) {
        this.books = books;
    }
}
