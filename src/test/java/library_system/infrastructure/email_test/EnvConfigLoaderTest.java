package library_system.infrastructure.email_test;

import library_system.infrastructure.email.EnvConfigLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class EnvConfigLoaderTest {

    @TempDir
    File tempDir;

    @Test
    void loadEnv_readsValidKeyValues() throws Exception {
        File envFile = new File(tempDir, "test.env");
        Files.write(envFile.toPath(), java.util.Arrays.asList(
                "# comment",
                "HOST = smtp.example.com",
                "PORT= 587",
                " USER = admin "
        ));

        Properties p = EnvConfigLoader.loadEnv(envFile.getAbsolutePath());

        assertEquals("smtp.example.com", p.getProperty("HOST"));
        assertEquals("587", p.getProperty("PORT"));
        assertEquals("admin", p.getProperty("USER"));
    }

    @Test
    void loadEnv_ignoresInvalidLines() throws Exception {
        File envFile = new File(tempDir, "test.env");
        Files.write(envFile.toPath(), java.util.Arrays.asList(
                "BADLINE",
                "NO_EQUALS",
                "KEY=VALUE"
        ));

        Properties p = EnvConfigLoader.loadEnv(envFile.getAbsolutePath());

        assertEquals(1, p.size());
        assertEquals("VALUE", p.getProperty("KEY"));
    }

    @Test
    void loadEnv_missingFile_returnsEmpty() {
        Properties p = EnvConfigLoader.loadEnv("missing-file.env");
        assertTrue(p.isEmpty());
    }

    @Test
    void load_missingResource_returnsEmpty() {
        Properties p = EnvConfigLoader.load("no-such-resource.env");
        assertTrue(p.isEmpty());
    }
    @Test
    void loadEnv_exceptionBranchCovered() {
    	
    	
        Properties p = EnvConfigLoader.loadEnv(tempDir.getAbsolutePath());
        assertTrue(p.isEmpty());
    }

    @Test
    void load_exceptionBranchCovered() {
        Properties p = EnvConfigLoader.load(null);
        assertTrue(p.isEmpty());
    }

    @Test
    void constructor_covered() {
        new EnvConfigLoader();
    }

}
