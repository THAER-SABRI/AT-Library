package library_system.application;

import java.time.LocalDate;
import java.util.List;

public interface BorrowLedger {
    void recordBorrow(String isbn, String userId, LocalDate dueDate);
    void recordReturn(String isbn, String userIdW);
    List<String> findAll();
}
