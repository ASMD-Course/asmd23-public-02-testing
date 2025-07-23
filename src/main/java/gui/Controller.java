package gui;

import java.util.Map;

public interface Controller {

    /**
     * Mark a Cell inside the grid
     * @param cell the position of the cell to makr
     */
    void mark(Pair<Integer, Integer> cell) throws IllegalStateException;

    /**
     * Check if the game is over
     * @return true if the game is over or false otherwise
     */
    boolean isOver();

    /**
     * Return a Map containing the Coordinates for the marked cells as key and the relative counter as value
     * @return a Map of Pairs that indicates the coordinates for the marked cells
     */
    Map<Pair<Integer, Integer>, Integer> getMarkedCells();
}
