package FakeImplementationForNeededInterfaces;

import library_system.application.BorrowLedger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FakeBorrowLedger implements BorrowLedger {
    public List<String> logs = new ArrayList<>();

    @Override
    public void recordBorrow(String isbn, String userId, LocalDate dueDate) {
        logs.add("BORROW|" + isbn + "|" + userId + "|" + dueDate);
    }

    @Override
    public void recordReturn(String isbn) {
        logs.add("RETURN|" + isbn);
    }

    @Override
    public List<String> findAll() {
        return logs;
    }
}
