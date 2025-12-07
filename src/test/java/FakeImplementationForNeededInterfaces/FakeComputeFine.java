package FakeImplementationForNeededInterfaces;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import library_system.application.ComputeFine;

public class FakeComputeFine extends ComputeFine {

    private Map<String, Double> map = new HashMap<>();

    public FakeComputeFine() {
        super(null, null);
    }

    public void setFine(String userId, double fine) {
        map.put(userId, fine);
    }

    @Override
    public double computeOutstanding(String userId, LocalDate today) {
        return map.getOrDefault(userId, 0.0);
    }
}
