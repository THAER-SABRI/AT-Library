package library_system.infrastructure.email_test;

import library_system.infrastructure.email.EnvConfigLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class EnvConfigLoaderTest {

    @TempDir
    File tempDir;

    @Test
    void loadsValidEnvLines() throws IOException {

        File envFile = new File(tempDir, "test.env");
        Files.write(envFile.toPath(), java.util.Arrays.asList(
                "# Comment",
                "",
                "EMAIL_HOST = smtp.test.com",
                "PORT=587",
                " USER = admin "
        ));

        Properties p = EnvConfigLoader.loadEnv(envFile.getAbsolutePath());

        assertEquals("smtp.test.com", p.getProperty("EMAIL_HOST"));
        assertEquals("587", p.getProperty("PORT"));
        assertEquals("admin", p.getProperty("USER"));
    }

    @Test
    void ignoresLinesWithoutEquals() throws IOException {

        File envFile = new File(tempDir, "test.env");
        Files.write(envFile.toPath(), java.util.Arrays.asList(
                "INVALIDLINE",
                "ANOTHER-BAD-LINE",
                "KEY=VALUE"
        ));

        Properties p = EnvConfigLoader.loadEnv(envFile.getAbsolutePath());

        assertEquals(1, p.size());
        assertEquals("VALUE", p.getProperty("KEY"));
    }

    @Test
    void returnsEmptyPropertiesWhenFileMissing() {

        Properties p = EnvConfigLoader.loadEnv("non_existent_file.env");

        // loader suppresses exceptions, so we just get an empty Properties
        assertTrue(p.isEmpty());
    }
}
