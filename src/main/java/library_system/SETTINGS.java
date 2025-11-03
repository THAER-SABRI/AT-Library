package library_system;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SETTINGS {
    public static int getLoanDays() {
        Map<String,String> m = read();
        String v = m.getOrDefault("LOAN_DAYS","28");
        try { return Integer.parseInt(v.trim()); } catch(Exception e) { return 28; }
    }

    public static void setLoanDays(int days) {
        Map<String,String> m = read();
        if (days <= 0) days = 28;
        m.put("LOAN_DAYS", String.valueOf(days));
        write(m);
    }

    public static void ensureUserExists(String id, String name, String phone, String email) {
        boolean exists = false;
        for (String line : STORAGE.readDataLines(STORAGE.USERS_FILE)) {
            String[] p = line.split("\\s*\\|\\s*", -1);
            if (p.length >= 1 && p[0].equals(id)) { exists = true; break; }
        }
        if (!exists) STORAGE.appendDataLine(STORAGE.USERS_FILE, id + " | " + name + " | " + phone + " | " + email);
    }

    private static Map<String,String> read() {
        Map<String,String> m = new LinkedHashMap<>();
        for (String line : STORAGE.readDataLines(STORAGE.SETTINGS_FILE)) {
            String[] p = line.split("=",2);
            if (p.length == 2) m.put(p[0].trim(), p[1].trim());
        }
        return m;
    }

    private static void write(Map<String,String> m) {
        List<String> out = new ArrayList<>();
        for (Map.Entry<String,String> e : m.entrySet()) out.add(e.getKey()+" = "+e.getValue());
        STORAGE.writeDataLines(STORAGE.SETTINGS_FILE, out);
    }
}
