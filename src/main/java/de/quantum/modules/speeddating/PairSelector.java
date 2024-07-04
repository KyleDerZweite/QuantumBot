package de.quantum.modules.speeddating;

import java.util.*;


public class PairSelector {

    public static Object[] selectBestPairs(Map<Integer, Map<Integer, Integer>> possibleMatches) {
        List<Integer> users = new ArrayList<>(possibleMatches.keySet());
        Set<Integer> paired = new HashSet<>();

        // Find pairs with a score of 0
        List<int[]> zeroPairs = new ArrayList<>();
        for (Integer user : users) {
            if (!paired.contains(user)) {
                for (Integer match : users) {
                    if (!paired.contains(match) && !user.equals(match) && possibleMatches.get(user).get(match) == 0) {
                        zeroPairs.add(new int[]{user, match});
                        paired.add(user);
                        paired.add(match);
                        break;
                    }
                }
            }
        }

        // Add zero score pairs to the final pairs list
        List<int[]> pairs = new ArrayList<>(zeroPairs);

        // Create a list of unpaired users
        List<Integer> unpairedUsers = new ArrayList<>();
        for (Integer user : users) {
            if (!paired.contains(user)) {
                unpairedUsers.add(user);
            }
        }

        // If there are still unpaired users, pair them with the lowest possible score
        if (unpairedUsers.size() > 1) {
            List<int[]> unpairedPairs = new ArrayList<>();
            for (Integer user : unpairedUsers) {
                for (Integer match : unpairedUsers) {
                    if (!user.equals(match)) {
                        unpairedPairs.add(new int[]{user, match, possibleMatches.get(user).get(match)});
                    }
                }
            }

            // Sort pairs by their score (third item in tuple)
            unpairedPairs.sort(Comparator.comparingInt(o -> o[2]));

            while (unpairedUsers.size() > 1) {
                for (int[] pair : unpairedPairs) {
                    if (unpairedUsers.contains(pair[0]) && unpairedUsers.contains(pair[1])) {
                        pairs.add(new int[]{pair[0], pair[1]});
                        unpairedUsers.remove(pair[0]);
                        unpairedUsers.remove(pair[1]);
                        break;
                    }
                }
            }
        }

        // If there's an odd user out, just leave them unpaired
        List<Integer> finalUnpairedUsers = unpairedUsers.size() == 1 ? unpairedUsers : new ArrayList<>();

        return new Object[]{pairs, finalUnpairedUsers, zeroPairs.size()};
    }
}

