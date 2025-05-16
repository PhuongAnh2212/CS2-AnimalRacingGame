package AnimalRaceGame;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

public class Leaderboard {
    public static ArrayList<LeaderboardEntry> entries = new ArrayList<>();
    public static void dummyData(){
         entries.add(new LeaderboardEntry("phanh", 4019L));
         entries.add(new LeaderboardEntry("tlt", 2503L));
         entries.add(new LeaderboardEntry("an", 2034L));
         entries.add(new LeaderboardEntry("ngoc", 3306L));

    }
    public static void recordResult(String name, long time) {
        entries.add(new LeaderboardEntry(name, time));
        // Sort the leaderboard by time in ascending order (fastest first)
        Collections.sort(entries);
    }

    public static void showLeaderboard(JFrame frame) {
        StringBuilder leaderboardText = new StringBuilder("Leaderboard:\n");

        for (int i = 0; i < entries.size(); i++) {
            leaderboardText.append(i + 1).append(". ")
                    .append(entries.get(i).getName())
                    .append(" - ")
                    .append(entries.get(i).getTime())
                    .append(" ms\n");
        }

        JOptionPane.showMessageDialog(frame, leaderboardText.toString(), "Leaderboard", JOptionPane.INFORMATION_MESSAGE);
    }

    // Helper class to store each entry's data
    static class LeaderboardEntry implements Comparable<LeaderboardEntry> {
        private String name;
        private long time;

        public LeaderboardEntry(String name, long time) {
            this.name = name;
            this.time = time;
        }

        public String getName() {
            return name;
        }

        public long getTime() {
            return time;
        }

        @Override
        public int compareTo(LeaderboardEntry other) {
            return Long.compare(this.time, other.time);
        }
    }
}
