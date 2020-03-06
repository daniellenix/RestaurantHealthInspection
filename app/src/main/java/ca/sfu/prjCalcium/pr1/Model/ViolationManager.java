package ca.sfu.prjCalcium.pr1.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represent a list of violations in an inspection.
 */
public class ViolationManager implements Iterable<Violation> {

    private List<Violation> violations = new ArrayList<>();

    public Violation getViolation(int position) {
        return violations.get(position);
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public boolean isEmpty() {
        return violations.isEmpty();
    }

    public void add(Violation v) {
        violations.add(v);
    }

    @Override
    public Iterator<Violation> iterator() {
        return violations.iterator();
    }
}
