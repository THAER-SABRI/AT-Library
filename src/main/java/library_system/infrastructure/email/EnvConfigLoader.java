package library_system.infrastructure.email;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class EnvConfigLoader {

    public static Properties loadEnv(String filePath) {
        Properties props = new Properties();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int idx = line.indexOf('=');
                if (idx == -1) continue;

                String key = line.substring(0, idx).trim();
                String value = line.substring(idx + 1).trim();

                props.setProperty(key, value);
            }

        } catch (IOException e) {
            System.out.println("Could not load .env file: " + e.getMessage());
        }

        return props;
    }
}
