package FakeImplementationForNeededInterfaces;

import library_system.application.OverdueBooks;
import library_system.domain.Book;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FakeOverdueBooks extends OverdueBooks {

    public List<Book> overdue = new ArrayList<>();

    public FakeOverdueBooks() {
        super(null, null);
    }

    @Override
    public List<Book> getOverdueBooks(LocalDate today) {
        return overdue;
    }
}
