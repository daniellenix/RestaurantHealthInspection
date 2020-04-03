package ca.sfu.prjCalcium.pr1.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class SearchResultList implements Iterable<Restaurant> {
    private static SearchResultList instance;
    private List<Restaurant> searchResult = new ArrayList<>();

    private SearchResultList() {
    }

    public static SearchResultList getInstance() {
        if (instance == null) {
            instance = new SearchResultList();
        }

        return instance;
    }

    public void getRestaurantsByName(String name) {
        RestaurantManager rManager = RestaurantManager.getInstance();

        if (isEmpty()) { // if result list empty, get all that satisfy the criteria, since this is either the only criteria or the first criteria{
            for (Restaurant r : rManager) {
                if (r.getRestaurantName().toLowerCase().contains(name.toLowerCase())) {
                    if (!searchResult.contains(r)) {
                        searchResult.add(r);
                    }
                }
            }
        } else { // if not empty, then user put more than one criteria, must filter the result list
            List<Restaurant> toRemove = new ArrayList<>();
            for (Restaurant r : searchResult) {
                if (!r.getRestaurantName().equals(name)) {
                    toRemove.add(r);
                }
            }
            searchResult.removeAll(toRemove);
        }

    }

    public void getRestaurantByMostRecentInspectionHazardLevel(String hazardLevel) {
        List<String> acceptableHazardLevels = new ArrayList<>(Arrays.asList("Low", "Moderate", "High"));

        if (!acceptableHazardLevels.contains(hazardLevel)) {
            return;
        }

        RestaurantManager rManager = RestaurantManager.getInstance();

        if (isEmpty()) {
            for (Restaurant r : rManager) {
                if (r.getInspections().isEmpty()) {
                    continue;
                }
                if (r.getInspections().getInspection(0).getHazardRating().equals(hazardLevel)) {
                    if (!searchResult.contains(r)) {
                        searchResult.add(r);
                    }
                }
            }
        } else {
            List<Restaurant> toRemove = new ArrayList<>();
            for (Restaurant r : searchResult) {
                if (r.getInspections().isEmpty()) {
                    toRemove.add(r);
                }
                if (!r.getInspections().getInspection(0).getHazardRating().equals(hazardLevel)) {
                    toRemove.add(r);
                }
            }
            searchResult.removeAll(toRemove);
        }

    }

    public void getRestaurantsWithLessThanNCriticalViolationsWithinLastYear(int number) {
        RestaurantManager rManager = RestaurantManager.getInstance();

        if (isEmpty()) {
            for (Restaurant r : rManager) {
                if (r.getInspections().isEmpty()) {
                    continue;
                }

                int numCriticalVio = 0;
                InspectionManager iManager = r.getInspections();
                long currentTime = System.currentTimeMillis();

                for (Inspection i : iManager) {
                    Date d = i.getInspectionDate();
                    long dInEpoch = d.getTime();
                    if (currentTime - dInEpoch <= 31556926) {
                        numCriticalVio += i.getNumCritical();
                    }
                }


                if (number <= numCriticalVio) {
                    if (!searchResult.contains(r)) {
                        searchResult.add(r);
                    }
                }
            }
        } else {
            List<Restaurant> toRemove = new ArrayList<>();
            for (Restaurant r : searchResult) {
                if (r.getInspections().isEmpty()) {
                    continue;
                }

                int numCriticalVio = 0;
                InspectionManager iManager = r.getInspections();
                long currentTime = System.currentTimeMillis();

                for (Inspection i : iManager) {
                    Date d = i.getInspectionDate();
                    long dInEpoch = d.getTime();
                    if (currentTime - dInEpoch <= 31556926) {
                        numCriticalVio += i.getNumCritical();
                    }
                }

                if (!(number <= numCriticalVio)) {
                    toRemove.add(r);
                }
            }
            searchResult.removeAll(toRemove);
        }
    }

    public void getRestaurantsWithMoreThanNCriticalViolationsWithinLastYear(int number) {
        RestaurantManager rManager = RestaurantManager.getInstance();

        if (isEmpty()) {
            for (Restaurant r : rManager) {
                if (r.getInspections().isEmpty()) {
                    continue;
                }

                int numCriticalVio = 0;
                InspectionManager iManager = r.getInspections();
                long currentTime = System.currentTimeMillis();

                for (Inspection i : iManager) {
                    Date d = i.getInspectionDate();
                    long dInEpoch = d.getTime();
                    if (currentTime - dInEpoch <= 31556926) {
                        numCriticalVio += i.getNumCritical();
                    }
                }

                if (number >= numCriticalVio) {
                    if (!searchResult.contains(r)) {
                        searchResult.add(r);
                    }
                }
            }
        } else {
            List<Restaurant> toRemove = new ArrayList<>();
            for (Restaurant r : searchResult) {
                if (r.getInspections().isEmpty()) {
                    toRemove.add(r);
                }

                int numCriticalVio = 0;
                InspectionManager iManager = r.getInspections();
                long currentTime = System.currentTimeMillis();

                for (Inspection i : iManager) {
                    Date d = i.getInspectionDate();
                    long dInEpoch = d.getTime();
                    if (currentTime - dInEpoch <= 31556926) {
                        numCriticalVio += i.getNumCritical();
                    }
                }

                if (!(number <= numCriticalVio)) {
                    toRemove.add(r);
                }
            }
            searchResult.removeAll(toRemove);
        }

    }

    public void getFavedRestaurants() {
        RestaurantManager rManager = RestaurantManager.getInstance();

        if (isEmpty()) {
            for (Restaurant r : rManager) {
                if (r.isFaved()) {
                    if (!searchResult.contains(r)) {
                        searchResult.add(r);
                    }
                }
            }
        } else {
            List<Restaurant> toRemove = new ArrayList<>();
            for (Restaurant r : searchResult) {
                if (!r.isFaved()) {
                    toRemove.add(r);
                }
            }
            searchResult.removeAll(toRemove);
        }
    }

    public boolean isEmpty() {
        return searchResult.isEmpty();
    }

    public void clear() {
        searchResult.clear();
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return searchResult.iterator();
    }
}
