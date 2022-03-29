package xyz.oribuin.auctionhouse.util;

public final class PluginUtils {


    /**
     * Parse a string into time in milliseconds
     *
     * @param time String to parse
     * @return Time in milliseconds
     */
    public static long parseTime(String time) {
        int seconds = 0;
        String[] split = time.split(" ");
        for (String s : split) {
            String[] split2 = s.split("");
            int amount = Integer.parseInt(split2[0]);
            String unit = split2[1];
            switch (unit) {
                case "d" -> seconds += amount * 86400;
                case "h" -> seconds += amount * 3600;
                case "m" -> seconds += amount * 60;
                case "s" -> seconds += amount;
            }
        }
        return (long) (seconds * 1000.0);
    }


    /**
     * Format a time in milliseconds into a string
     *
     * @param time Time in milliseconds
     * @return Formatted time
     */
    public static String formatTime(int time) {
        long totalSeconds = time / 1000;
        if (totalSeconds <= 0) {
            return "0s";
        }

        long days = (long) Math.floor(totalSeconds / 86400.0);
        totalSeconds %= 86400;

        long hours = (long) Math.floor(totalSeconds / 3600.0);
        totalSeconds %= 3600;

        long minutes = (long) Math.floor(totalSeconds / 60.0);
        long seconds = totalSeconds % 60;

        StringBuilder builder = new StringBuilder();
        if (days > 0) {
            builder.append(days).append("d, ");
        }

        builder.append(hours).append(", ");
        builder.append(minutes).append(", ");
        builder.append(seconds).append("s");

        return builder.toString();
    }
}
