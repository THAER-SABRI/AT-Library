package FakeImplementationForNeededInterfaces;

import library_system.application.BorrowLedger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FakeBorrowLedger implements BorrowLedger {

    public List<String> logs = new ArrayList<>();

    @Override
    public void recordBorrow(String id, String userId, LocalDate dueDate) {
        logs.add("BORROW|" + id + "|" + userId + "|" + dueDate);
    }

    @Override
    public void recordReturn(String id, String userId) {
        logs.add("RETURN|" + id + "|" + userId);
    }

    @Override
    public List<String> findAll() {
        return logs;
    }
}
