package library_system.infrastructure.email;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Utility class for loading environment-style configuration files.
 * <p>
 * This class supports two different loading mechanisms:
 * <ul>
 *     <li>{@link #loadEnv(String)} – loads key/value pairs from a file on disk</li>
 *     <li>{@link #load(String)} – loads key/value pairs from a classpath resource</li>
 * </ul>
 * <p>
 * The expected format is simple <code>key=value</code> pairs,
 * with optional comments beginning with {@code #}. Blank lines
 * and malformed entries are ignored.
 * </p>
 */
public class EnvConfigLoader {

    /**
     * Loads environment configuration from a file system path.
     * <p>
     * Each non-empty, non-comment line must contain exactly one {@code '='}
     * separating the key and the value. Lines failing this format are skipped.
     * </p>
     *
     * @param filePath the path to the configuration file on disk
     * @return a {@link Properties} object containing all loaded key/value pairs;
     *         never {@code null}
     */
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

    /**
     * Loads configuration from a classpath resource.
     * <p>
     * This method is useful when configuration files are bundled inside the
     * application's JAR or resource directory. The resource is loaded via the
     * class loader; if not found, an empty {@link Properties} object is returned.
     * </p>
     *
     * @param resourceName the name of the resource located in the classpath
     * @return a {@link Properties} object containing loaded key/value pairs,
     *         or an empty one if the resource cannot be found
     */
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
