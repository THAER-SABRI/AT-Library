package FakeImplementationForNeededInterfaces;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import library_system.application.OverdueMedia;
import library_system.domain.Media;

public class FakeOverdueMedia extends OverdueMedia {

    public List<Media> overdue = new ArrayList<>();

    public FakeOverdueMedia() {
        super(new FakeBookRepo(), new FakeCdRepo());
    }

    @Override
    public List<Media> getAllOverdues(LocalDate today) {
        return overdue;
    }

    @Override
    public List<Media> getOverdueForUser(String userId, LocalDate today) {
        List<Media> result = new ArrayList<>();
        for (Media m : overdue) {
            if (userId.equals(m.getBorrowerId())) result.add(m);
        }
        return result;
    }

    @Override
    public boolean userHasOverdues(String userId, LocalDate today) {
        for (Media m : overdue) {
            if (userId.equals(m.getBorrowerId())) return true;
        }
        return false;
    }
}
