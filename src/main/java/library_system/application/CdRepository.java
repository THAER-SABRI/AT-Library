package library_system.application;

import java.util.List;
import library_system.domain.CD;

public interface CdRepository {
    List<CD> getAll();
    void saveAll(List<CD> cds);
}
