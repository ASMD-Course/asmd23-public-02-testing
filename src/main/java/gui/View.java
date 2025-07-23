package gui;

import javax.swing.*;
import java.util.Map;

public interface View {
    /**
     * Shows the view to the user
     */
    void start();

    /**
     * Closes the view
     */
    void close();

    /**
     * Handles a click event on the cell at the given coordinates.
     * @param cell The (x, y) position of the clicked cell.
     */
    void handleCellClick(Pair<Integer, Integer> cell);

    /**
     * Function for closing the application
     */
    default void exitApplication() {
        System.exit(0);
    }

    /**
     * Return the cells of the grid
     * @return a Map where the key are the grid button and the values are the coords of these
     */
    Map<JButton, Pair<Integer, Integer>> getCells();
}
