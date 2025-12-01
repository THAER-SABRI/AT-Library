package library_system.infrastructure.persistence_test;

import library_system.infrastructure.persistence.FilePaymentLedger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilePaymentLedgerTest {

    @TempDir
    Path tempDir;

    Path file;
    FilePaymentLedger ledger;

    @BeforeEach
    void setup() {
        file = tempDir.resolve("PAYMENTS.TXT");
        ledger = new FilePaymentLedger(file.toString());
    }

    @Test
    void createsHeaderWhenFileMissing() throws Exception {
        ledger.recordPayment("U1", 50.0, LocalDate.of(2024, 1, 1));
        assertTrue(Files.exists(file));

        List<String> lines = Files.readAllLines(file);
        assertEquals(2, lines.size());
        assertTrue(lines.get(0).contains("USER_ID"));
    }

    @Test
    void recordsPaymentCorrectly() throws Exception {
        LocalDate date = LocalDate.of(2024, 1, 10);
        ledger.recordPayment("U1", 12.5, date);

        List<String> lines = Files.readAllLines(file);
        assertEquals(2, lines.size());
        assertTrue(lines.get(1).contains("U1"));
        assertTrue(lines.get(1).contains("12.50"));
        assertTrue(lines.get(1).contains(date.toString()));
    }

    @Test
    void findAllReturnsAllLines() {
        ledger.recordPayment("A", 5, LocalDate.now());
        ledger.recordPayment("B", 7, LocalDate.now());

        List<String> list = ledger.findAll();
        assertEquals(3, list.size());
    }

    @Test
    void deleteAllClearsFileAndAddsHeader() throws Exception {
        ledger.recordPayment("U1", 99, LocalDate.now());
        ledger.deleteAll();

        List<String> lines = Files.readAllLines(file);
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("USER_ID"));
    }

    @Test
    void invalidInputsIgnored() {
        ledger.recordPayment(null, 10, LocalDate.now());
        ledger.recordPayment(" ", 10, LocalDate.now());
        ledger.recordPayment("X", 5, null);

        assertEquals(0, ledger.findAll().size());
    }
}
