package ca.sfu.prjCalcium.pr1.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Represent a list of inspections for a restaurant.
 */
public class InspectionManager implements Iterable<Inspection> {

    private List<Inspection> inspections;

    {
        inspections = new ArrayList<>();
    }

    public Inspection getInspection(int position) {
        return inspections.get(position);
    }

    public List<Inspection> getInspections() {
        return inspections;
    }

    public boolean isEmpty() {
        return inspections.isEmpty();
    }

    public void add(Inspection i) {
        inspections.add(i);
    }

    @Override
    public Iterator<Inspection> iterator() {
        return inspections.iterator();
    }

    public void sort(Comparator<Inspection> c) {
        Collections.sort(inspections, c);
    }
}
