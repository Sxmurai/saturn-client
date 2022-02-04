/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.internal;

public class Stopwatch {
    // got from a quick google search
    public static final long MS_TO_NS = 1000000L;
    public static final long TICK_MS = 50L;

    private long time = -1L;

    /**
     * Resets the clock to the current time
     */
    public void reset() {
        time = System.nanoTime();
    }

    /**
     * Checks if an amount of time in ticks has passed
     * @param ticks The duration in ticks
     * @return if the time has passed
     */
    public boolean passedTicks(int ticks) {
        return passedMs(ticks * TICK_MS);
    }

    /**
     * Checks if an amount of time in seconds has passed
     * @param s The duration in seconds
     * @return if the time has passed
     */
    public boolean passedS(double s) {
        return passedNs((long) (s * 1000.0));
    }

    /**
     * Checks if an amount of time in milliseconds has passed
     * @param ms The duration in milliseconds
     * @return if the time has passed
     */
    public boolean passedMs(long ms) {
        return passedNs(ms * MS_TO_NS);
    }

    /**
     * Checks to see if an amount of time in nanoseconds has passed
     * @param ns The duration in nanoseconds
     * @return if the time has passed
     */
    public boolean passedNs(long ns) {
        return System.nanoTime() - time >= ns;
    }
}
