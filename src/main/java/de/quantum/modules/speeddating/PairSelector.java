package de.quantum.modules.speeddating;

import de.quantum.modules.speeddating.entities.SpeedDatingUser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class PairSelector {

    public static MatchingResult getBestMatches(ConcurrentHashMap<String, SpeedDatingUser> tMap) {
        List<String> users = new ArrayList<>(tMap.keySet());
        Set<String> paired = new HashSet<>();

        // Find pairs with a score of 0
        List<String[]> zeroPairs = new ArrayList<>();
        for (String user : users) {
            if (!paired.contains(user)) {
                for (String match : users) {
                    if (!paired.contains(match) && !user.equals(match) && tMap.get(user).getMatchHistory().containsKey(match) && tMap.get(user).getMatchHistory().get(match) == 0) {
                        zeroPairs.add(new String[]{user, match});
                        paired.add(user);
                        paired.add(match);
                        break;
                    }
                }
            }
        }

        // Add zero score pairs to the final pairs list
        List<String[]> pairs = new ArrayList<>(zeroPairs);

        // Create a list of unpaired users
        List<String> unpairedUsers = new ArrayList<>();
        for (String user : users) {
            if (!paired.contains(user)) {
                unpairedUsers.add(user);
            }
        }

        // If there are still unpaired users, pair them with the lowest possible score
        if (unpairedUsers.size() > 1) {
            List<Object[]> unpairedPairs = new ArrayList<>();
            for (String user : unpairedUsers) {
                for (String match : unpairedUsers) {
                    if (!user.equals(match) && tMap.get(user).getMatchHistory().containsKey(match)) {
                        unpairedPairs.add(new Object[]{user, match, tMap.get(user).getMatchHistory().get(match)});
                    }
                }
            }

            // Sort pairs by their score (third item in tuple)
            unpairedPairs.sort(Comparator.comparingInt(o -> (int) o[2]));

            while (unpairedUsers.size() > 1) {
                for (Object[] pair : unpairedPairs) {
                    if (unpairedUsers.contains((String) pair[0]) && unpairedUsers.contains((String) pair[1])) {
                        pairs.add(new String[]{(String) pair[0], (String) pair[1]});
                        unpairedUsers.remove((String) pair[0]);
                        unpairedUsers.remove((String) pair[1]);
                        break;
                    }
                }
            }
        }

        // If there's an odd user out, just leave them unpaired
        List<String> finalUnpairedUsers = unpairedUsers.size() == 1 ? unpairedUsers : new ArrayList<>();
        return new MatchingResult(pairs, finalUnpairedUsers, zeroPairs.size());
    }

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

    public static void testSelectBestPairs() {
        Map<Integer, Map<Integer, Integer>> possibleMatches = new HashMap<>();

        // Example 1: Simple case with 4 users
        possibleMatches.put(1, new HashMap<>());
        possibleMatches.get(1).put(2, 0);
        possibleMatches.get(1).put(3, 1);
        possibleMatches.get(1).put(4, 2);

        possibleMatches.put(2, new HashMap<>());
        possibleMatches.get(2).put(1, 0);
        possibleMatches.get(2).put(3, 2);
        possibleMatches.get(2).put(4, 3);

        possibleMatches.put(3, new HashMap<>());
        possibleMatches.get(3).put(1, 1);
        possibleMatches.get(3).put(2, 2);
        possibleMatches.get(3).put(4, 0);

        possibleMatches.put(4, new HashMap<>());
        possibleMatches.get(4).put(1, 2);
        possibleMatches.get(4).put(2, 3);
        possibleMatches.get(4).put(3, 0);

        Object[] result = selectBestPairs(possibleMatches);
        List<int[]> pairs = (List<int[]>) result[0];
        List<Integer> unpairedUsers = (List<Integer>) result[1];
        int zeroPairsCount = (int) result[2];

        System.out.println("Pairs: " + pairs);
        System.out.println("Unpaired users: " + unpairedUsers);
        System.out.println("Zero pairs count: " + zeroPairsCount);

        // Example 2: Case with 5 users
        possibleMatches.clear();

        possibleMatches.put(1, new HashMap<>());
        possibleMatches.get(1).put(2, 0);
        possibleMatches.get(1).put(3, 1);
        possibleMatches.get(1).put(4, 2);
        possibleMatches.get(1).put(5, 3);

        possibleMatches.put(2, new HashMap<>());
        possibleMatches.get(2).put(1, 0);
        possibleMatches.get(2).put(3, 2);
        possibleMatches.get(2).put(4, 3);
        possibleMatches.get(2).put(5, 1);

        possibleMatches.put(3, new HashMap<>());
        possibleMatches.get(3).put(1, 1);
        possibleMatches.get(3).put(2, 2);
        possibleMatches.get(3).put(4, 0);
        possibleMatches.get(3).put(5, 2);

        possibleMatches.put(4, new HashMap<>());
        possibleMatches.get(4).put(1, 2);
        possibleMatches.get(4).put(2, 3);
        possibleMatches.get(4).put(3, 0);
        possibleMatches.get(4).put(5, 1);

        possibleMatches.put(5, new HashMap<>());
        possibleMatches.get(5).put(1, 3);
        possibleMatches.get(5).put(2, 1);
        possibleMatches.get(5).put(3, 2);
        possibleMatches.get(5).put(4, 1);

        result = selectBestPairs(possibleMatches);
        pairs = (List<int[]>) result[0];
        unpairedUsers = (List<Integer>) result[1];
        zeroPairsCount = (int) result[2];

        System.out.println("Pairs: " + pairs);
        System.out.println("Unpaired users: " + unpairedUsers);
        System.out.println("Zero pairs count: " + zeroPairsCount);
    }


    public record MatchingResult(List<String[]> pairs, List<String> finalUnpairedUsers, int zeroPairsCount) {}
}

