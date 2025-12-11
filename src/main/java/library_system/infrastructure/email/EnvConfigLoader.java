package library_system.infrastructure.email;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class EnvConfigLoader {

    public static Properties loadEnv(String filePath) {
        Properties props = new Properties();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int idx = line.indexOf('=');
                if (idx == -1) continue;
                String key = line.substring(0, idx).trim();
                String value = line.substring(idx + 1).trim();
                props.setProperty(key, value);
            }
        } catch (Exception ignored) {}

        return props;
    }

    public static Properties load(String resourceName) {
        Properties props = new Properties();

        try {
            InputStream in = EnvConfigLoader.class.getClassLoader()
                    .getResourceAsStream(resourceName);
            if (in == null) return props;

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int idx = line.indexOf('=');
                if (idx == -1) continue;
                String key = line.substring(0, idx).trim();
                String value = line.substring(idx + 1).trim();
                props.setProperty(key, value);
            }
        } catch (Exception ignored) {}

        return props;
    }
}
