package library_system.infrastructure.persistence_test;

import library_system.infrastructure.persistence.FileSettingsGateway;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class FileSettingsGatewayTest {

    @TempDir
    Path tempDir;

    Path file;
    FileSettingsGateway settings;

    @BeforeEach
    void setup() {
        file = tempDir.resolve("SETTINGS.TXT");
        settings = new FileSettingsGateway(file.toString());
    }

    @Test
    void defaultValueCreatedWhenFileMissing() throws Exception {
        int days = settings.getLoanDays();
        assertEquals(28, days);

        assertTrue(Files.exists(file));
        String content = Files.readString(file);
        assertTrue(content.contains("loanDays=28"));
    }

    @Test
    void savesUpdatedLoanDays() throws Exception {
        settings.setLoanDays(15);

        String data = Files.readString(file);
        assertTrue(data.contains("loanDays=15"));
        assertEquals(15, settings.getLoanDays());
    }

    @Test
    void ignoresInvalidValuesInFile() throws Exception {
        Files.writeString(file, "loanDays=abc");

        assertEquals(28, settings.getLoanDays());
    }

    @Test
    void rejectsZeroOrNegativeValues() throws Exception {
        settings.setLoanDays(-5);
        settings.setLoanDays(0);

        assertEquals(28, settings.getLoanDays());
    }

    @Test
    void loadsExistingValidValue() throws Exception {
        Files.writeString(file, "loanDays=12");

        assertEquals(12, settings.getLoanDays());
    }
}
