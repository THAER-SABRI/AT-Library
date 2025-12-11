package FakeImplementationForNeededInterfaces;

import java.util.ArrayList;
import java.util.List;

import library_system.application.CdRepository;
import library_system.domain.CD;

public class FakeCdRepo implements CdRepository {

    public List<CD> cds = new ArrayList<>();

    @Override
    public List<CD> getAll() {
        return cds;
    }

    @Override
    public void saveAll(List<CD> cds) {
        this.cds = cds;
    }
}
