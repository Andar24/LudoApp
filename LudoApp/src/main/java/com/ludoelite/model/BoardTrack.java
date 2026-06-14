package com.ludoelite.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the Ludo board track system with 52 main positions
 * plus home lanes and safe zones.
 * 
 * Demonstrates:
 * - Encapsulation: Track positions and safe zones are encapsulated
 * - Single Responsibility: Only handles board track logic
 */
public class BoardTrack {
    
    // Safe zones (cannot be captured)
    private static final int[] SAFE_POSITIONS = {0, 8, 13, 21, 26, 34, 39, 47};
    
    // Starting positions for each color
    private static final Map<PlayerColor, Integer> START_POSITIONS = new HashMap<>();
    
    static {
        START_POSITIONS.put(PlayerColor.RED, 0);
        START_POSITIONS.put(PlayerColor.GREEN, 13);
        START_POSITIONS.put(PlayerColor.YELLOW, 26);
        START_POSITIONS.put(PlayerColor.BLUE, 39);
    }
    
    // Home lane entry positions (where pieces turn into home lane)
    private static final Map<PlayerColor, Integer> HOME_ENTRY_POSITIONS = new HashMap<>();
    
    static {
        HOME_ENTRY_POSITIONS.put(PlayerColor.RED, 50);
        HOME_ENTRY_POSITIONS.put(PlayerColor.GREEN, 11);
        HOME_ENTRY_POSITIONS.put(PlayerColor.YELLOW, 24);
        HOME_ENTRY_POSITIONS.put(PlayerColor.BLUE, 37);
    }
    
    public static final int MAIN_TRACK_SIZE = 52;
    public static final int HOME_LANE_SIZE = 6;
    
    /**
     * Checks if a position is a safe zone.
     */
    public static boolean isSafeZone(int position) {
        for (int safePos : SAFE_POSITIONS) {
            if (safePos == position) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets the starting position for a given color.
     */
    public static int getStartPosition(PlayerColor color) {
        return START_POSITIONS.getOrDefault(color, 0);
    }
    
    /**
     * Gets the home entry position for a given color.
     */
    public static int getHomeEntryPosition(PlayerColor color) {
        return HOME_ENTRY_POSITIONS.getOrDefault(color, 0);
    }
    
    /**
     * Normalizes track position (wraps around at 52).
     */
    public static int normalizePosition(int position) {
        if (position < 0) {
            return 0;
        }
        return position % MAIN_TRACK_SIZE;
    }
    
    /**
     * Calculates absolute position from player's perspective.
     * Each player sees position 0 as their starting point.
     */
    public static int getAbsolutePosition(PlayerColor color, int relativePosition) {
        int start = getStartPosition(color);
        return normalizePosition(start + relativePosition);
    }
    
    /**
     * Checks if the piece should enter home lane at current position.
     */
    public static boolean shouldEnterHomeLane(PlayerColor color, int currentPosition) {
        return currentPosition == getHomeEntryPosition(color);
    }
}
