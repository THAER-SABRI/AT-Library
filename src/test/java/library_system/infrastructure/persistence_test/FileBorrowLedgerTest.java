package library_system.infrastructure.persistence_test;

import library_system.infrastructure.persistence.FileBorrowLedger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class FileBorrowLedgerTest {

    @TempDir
    Path tempDir;

    Path ledgerFile;
    FileBorrowLedger ledger;

    @BeforeEach
    void setup() {
        ledgerFile = tempDir.resolve("BORROWS.TXT");
        ledger = new FileBorrowLedger(ledgerFile.toString());
    }

    @Test
    void recordBorrowWritesCorrectLine() throws Exception {
        LocalDate due = LocalDate.of(2024, 1, 5);

        ledger.recordBorrow("111", "U1", due);

        List<String> lines = Files.readAllLines(ledgerFile);
        assertEquals(2, lines.size());

        String entry = lines.get(1);
        assertTrue(entry.contains("BORROW"));
        assertTrue(entry.contains("111"));
        assertTrue(entry.contains("U1"));
        assertTrue(entry.contains("2024-01-05"));
    }

    @Test
    void recordReturnWritesCorrectLine() throws Exception {
        ledger.recordReturn("222", "U1");

        List<String> lines = Files.readAllLines(ledgerFile);
        assertEquals(2, lines.size());

        String entry = lines.get(1);
        assertTrue(entry.contains("RETURN"));
        assertTrue(entry.contains("222"));
        assertTrue(entry.contains("U1"));
    }

    @Test
    void findAllReadsLines() throws Exception {
        Files.write(ledgerFile, Arrays.asList(
                "| TYPE | ISBN | USER_ID | DUE_DATE |",
                "LINE1",
                "LINE2"
        ));

        List<String> result = ledger.findAll();

        assertEquals(3, result.size());
        assertEquals("LINE1", result.get(1));
    }

    @Test
    void reloadFromFileReplacesTargetList() {
        ledger.recordBorrow("333", "X", LocalDate.now());

        List<String> target = new ArrayList<>();
        target.add("OLD");

        ledger.reloadFromFile(target);

        assertEquals(2, target.size());
        assertTrue(target.get(1).contains("333"));
    }

    @Test
    void deleteAllClearsFileButKeepsHeader() throws Exception {
        ledger.recordBorrow("111", "U1", LocalDate.now());

        ledger.deleteAll();

        List<String> lines = Files.readAllLines(ledgerFile);
        assertEquals(1, lines.size()); 
        assertTrue(lines.get(0).contains("TYPE"));
    }

    @Test
    void nullInputsDoNothing() {
        ledger.recordBorrow(null, "U1", LocalDate.now());
        ledger.recordBorrow("111", null, LocalDate.now());
        ledger.recordBorrow("111", "U1", null);
        ledger.recordReturn(null, "U1");
        ledger.recordReturn("111", null);

        List<String> lines = ledger.findAll();
        assertEquals(0, lines.size());
    }

    @Test
    void ensureHeaderCreatesHeaderWhenMissing() {
        ledger.findAll(); 

        ledger.recordBorrow("111", "U1", LocalDate.now());

        List<String> lines = ledger.findAll();
        assertEquals(2, lines.size());
        assertTrue(lines.get(0).contains("TYPE"));
    }
}
