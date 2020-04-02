package ca.sfu.prjCalcium.pr1.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SearchResultList {
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

        if(name != "") {
            for (Restaurant r : rManager) {
                if (r.getRestaurantName().toLowerCase().contains(name.toLowerCase())) {
                    if (!searchResult.contains(r)) {
                        searchResult.add(r);
                    }
                }
            }
        }
        else{
            return;
        }
    }

    public void getRestaurantByMostRecentInspectionHazardLevel(String hazardLevel) {
        List<String> acceptableHazardLevels = new ArrayList<>(Arrays.asList("Low", "Moderate", "High"));

        if (!acceptableHazardLevels.contains(hazardLevel)) {
            return;
        }

        RestaurantManager rManager = RestaurantManager.getInstance();

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
    }

    public void getRestaurantsWithLessThanNCriticalViolationsWithinLastYear(int number) {
        RestaurantManager rManager = RestaurantManager.getInstance();

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

            if(number != 0) {
                if (number <= numCriticalVio) {
                    if (!searchResult.contains(r)) {
                        searchResult.add(r);
                    }
                }
            }
            else{
                return;
            }
        }
    }

    public void getRestaurantsWithMoreThanNCriticalViolationsWithinLastYear(int number) {
        RestaurantManager rManager = RestaurantManager.getInstance();

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

            if (number != 0) {
                if (number >= numCriticalVio) {
                    if (!searchResult.contains(r)) {
                        searchResult.add(r);
                    }
                }
            }
            else{
                return;
            }
        }
    }

    public void getFavedRestaurants() {
        RestaurantManager rManager = RestaurantManager.getInstance();

        for (Restaurant r : rManager) {
            if (r.isFaved()) {
                if (!searchResult.contains(r)) {
                    searchResult.add(r);
                }
            }
        }
    }

    public void clear() {
        searchResult.clear();
    }
}
