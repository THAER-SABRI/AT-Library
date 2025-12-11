package library_system.infrastructure.persistence_test;

import library_system.domain.CD;
import library_system.domain.strategy.BorrowDurationStrategy;
import library_system.domain.strategy.FineStrategy;
import library_system.infrastructure.persistence.FileCdRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileCdRepositoryTest {

    @TempDir
    Path temp;

    Path file;
    FileCdRepository repo;

    BorrowDurationStrategy borrow = () -> 10;
    FineStrategy fine = () -> 2;

    @BeforeEach
    void setup() {
        file = temp.resolve("CDS.TXT");
        repo = new FileCdRepository(file.toString(), borrow, fine);
    }

    CD cd(String id, String title) {
        return new CD(id, title, borrow, fine);
    }

    @Test
    void saveAndLoadFreeCd() {
        CD c = cd("CD1", "Music");
        repo.saveAll(List.of(c));

        List<CD> list = repo.getAll();
        assertEquals(1, list.size());
        CD x = list.get(0);

        assertEquals("CD1", x.getId());
        assertEquals("Music", x.getTitle());
        assertFalse(x.isBorrowed());
        assertNull(x.getBorrowerId());
        assertNull(x.getDueDate());
    }

    @Test
    void saveAndLoadBorrowedCd() {
        CD c = cd("CD2", "Album");
        c.borrow("U1", LocalDate.of(2024,1,10));

        repo.saveAll(List.of(c));
        List<CD> list = repo.getAll();

        assertEquals(1, list.size());
        CD x = list.get(0);

        assertEquals("CD2", x.getId());
        assertTrue(x.isBorrowed());
        assertEquals("U1", x.getBorrowerId());
        assertEquals(LocalDate.of(2024,1,10), x.getDueDate());
    }

    @Test
    void malformedLinesIgnored() throws Exception {
        Files.write(file, List.of(
                "BAD LINE",
                "| a | b | c |",
                "| ID | TITLE | STATUS | USER_ID | DUE_DATE |"
        ));

        List<CD> list = repo.getAll();
        assertTrue(list.isEmpty());
    }

    @Test
    void multipleCdsReadCorrectly() {
        CD c1 = cd("A", "T1");
        CD c2 = cd("B", "T2");
        c2.borrow("X", LocalDate.of(2025,5,5));

        repo.saveAll(List.of(c1, c2));
        List<CD> list = repo.getAll();

        assertEquals(2, list.size());

        CD x1 = list.stream().filter(c -> c.getId().equals("A")).findFirst().get();
        CD x2 = list.stream().filter(c -> c.getId().equals("B")).findFirst().get();

        assertFalse(x1.isBorrowed());

        assertTrue(x2.isBorrowed());
        assertEquals("X", x2.getBorrowerId());
        assertEquals(LocalDate.of(2025,5,5), x2.getDueDate());
    }

    @Test
    void saveCreatesParentFolder() {
        Path nested = temp.resolve("deep/dir/CDS.TXT");
        FileCdRepository r2 = new FileCdRepository(nested.toString(), borrow, fine);

        CD c = cd("CD9", "Test");
        r2.saveAll(List.of(c));

        assertTrue(Files.exists(nested));
        assertEquals(1, r2.getAll().size());
    }

    @Test
    void nullUserAndNullDateHandledCorrectly() throws Exception {
        Files.write(file, List.of(
                "| ID | TITLE | STATUS | USER_ID | DUE_DATE |",
                "| CDX | Chill | BORROWED | null | null |"
        ));

        List<CD> list = repo.getAll();
        assertEquals(1, list.size());

        CD c = list.get(0);
        assertTrue(c.isBorrowed());
        assertNull(c.getBorrowerId());
        assertNull(c.getDueDate());
    }
}
