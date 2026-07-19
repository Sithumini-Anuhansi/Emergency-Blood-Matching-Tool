package service;

import graph.Dijkstra;
import model.Donor;
import model.DonorMatch;
import model.Location;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DonorMatchingService {

    private DonorService donorService;

    private Dijkstra dijkstra;

    public DonorMatchingService(
            DonorService donorService,
            Dijkstra dijkstra
    ) {

        this.donorService = donorService;
        this.dijkstra = dijkstra;

    }

    /*
     * Find best matching donors
     */
    public List<DonorMatch> findBestDonors(
            String bloodGroup,
            Location hospitalLocation
    ) {

        List<Donor> eligibleDonors =
                donorService.findEligibleDonors(
                        bloodGroup
                );

        List<DonorMatch> matches =
                new ArrayList<>();

        for (Donor donor : eligibleDonors) {

            double distance =
                    dijkstra.getShortestDistance(
                            hospitalLocation,
                            donor.getLocation()
                    );

            /*
             * Ignore unreachable donors
             */
            if (distance != Double.MAX_VALUE
                    && !Double.isInfinite(distance)) {

                matches.add(
                        new DonorMatch(
                                donor,
                                distance
                        )
                );

            }

        }

        /*
         * Sort nearest donors first
         */
        matches.sort(
                Comparator.comparingDouble(
                        DonorMatch::getDistance
                )
        );

        /*
         * Return maximum 10 donors
         */
        if (matches.size() > 10) {

            return new ArrayList<>(
                    matches.subList(0, 10)
            );

        }

        return matches;

    }

}