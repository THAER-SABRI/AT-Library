package FakeImplementationForNeededInterfaces;

import java.util.ArrayList;
import java.util.List;

import library_system.application.BookRepository;
import library_system.domain.Book;

public class FakeBookRepo implements BookRepository {

    public List<Book> books = new ArrayList<>();

    @Override
    public List<Book> getAll() {
        return books;
    }

    @Override
    public void saveAll(List<Book> books) {
        this.books = new ArrayList<>(books);
    }
    

}
