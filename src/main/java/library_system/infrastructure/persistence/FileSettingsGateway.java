package library_system.infrastructure.persistence;

import library_system.application.SettingsGateway;

import java.io.IOException;
import java.nio.file.*;

public class FileSettingsGateway implements SettingsGateway {

    private final String file;

    public FileSettingsGateway(String file) {
        this.file = file;
    }

    @Override
    public int getLoanDays() {
        ensureFileExists();

        try {
            String content = Files.readString(Paths.get(file));
            String[] parts = content.split("=");
            if (parts.length == 2) {
                int value = Integer.parseInt(parts[1].trim());
                if (value > 0) return value;
            }
        } catch (Exception ignored) {}

        setLoanDays(28);
        return 28;
    }

    @Override
    public void setLoanDays(int days) {
        if (days <= 0) {
            write("loanDays=28");
            return;
        }
        write("loanDays=" + days);
    }

    private void ensureFileExists() {
        Path p = Paths.get(file);
        if (!Files.exists(p)) {
            write("loanDays=28");
        }
    }

    private void write(String s) {
        try {
            Files.writeString(Paths.get(file), s);
        } catch (IOException ignored) {}
    }

    @Override
    public int getCdLoanDays() {
        return getLoanDays();
    }

    @Override
    public void setCdLoanDays(int days) {
        setLoanDays(days);
    }
}
