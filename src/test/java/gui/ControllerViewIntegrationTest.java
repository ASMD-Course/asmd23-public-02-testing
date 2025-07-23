package gui;

import gui.logger.Logger;
import gui.logger.LogLevel;
import org.junit.jupiter.api.*;

import javax.swing.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ControllerViewIntegrationTest {

    private static final int SIZE = 8;

    private Controller controller;
    private GUI gui;
    private boolean exitCalled = false;

    @BeforeEach
    void setUp() {
        controller = new ControllerImpl(SIZE, new TestLogger());
        gui = new GUI(SIZE, controller) {
            @Override
            public void exitApplication() {
                exitCalled = true;
            }
        };
    }


    @Test
    @DisplayName("Clicking a cell marks it and updates GUI")
    void testCellClickUpdatesControllerAndView() {
        Pair<Integer, Integer> cell = new Pair<>(1, 1);

        assertTrue(controller.getMarkedCells().isEmpty());

        gui.handleCellClick(cell);

        Map<Pair<Integer, Integer>, Integer> marked = controller.getMarkedCells();
        assertEquals(1, marked.size());
        assertEquals(0, marked.get(cell));

        JButton clickedButton = getButtonForCell(cell);
        assertNotNull(clickedButton);
        assertEquals("0", clickedButton.getText());
    }

    @Test
    @DisplayName("Clicking adjacent cell triggers translation")
    void testTranslationOfAdjacentCells() {
        Pair<Integer, Integer> first = new Pair<>(1, 1);
        Pair<Integer, Integer> adjacent = new Pair<>(1, 2);
        Pair<Integer, Integer> expectedTranslated = new Pair<>(2, 0);

        gui.handleCellClick(first);
        gui.handleCellClick(adjacent);

        assertFalse(controller.getMarkedCells().containsKey(first));
        assertTrue(controller.getMarkedCells().containsKey(expectedTranslated));

        JButton translatedButton = getButtonForCell(expectedTranslated);
        assertNotNull(translatedButton);
        assertEquals("0", translatedButton.getText());
    }

    @Test
    @DisplayName("Game ends when translation goes out of bounds")
    void testGameEndsWhenTranslatedOutOfBounds() {
        Pair<Integer, Integer> base = new Pair<>(SIZE - 2, 0);
        Pair<Integer, Integer> adjacent = new Pair<>(SIZE - 1, 0 );

        gui.handleCellClick(base);
        assertFalse(controller.isOver());

        gui.handleCellClick(adjacent);
        assertTrue(controller.isOver());
        assertTrue(exitCalled);
    }

    @Test
    @DisplayName("Clicking multiple non-adjacent cells increments value")
    void testMultipleIndependentClicks() {
        Pair<Integer, Integer> first = new Pair<>(0, 0);
        Pair<Integer, Integer> second = new Pair<>(3, 3);

        gui.handleCellClick(first);
        gui.handleCellClick(second);

        var marked = controller.getMarkedCells();
        assertEquals(2, marked.size());
        assertEquals(0, marked.get(first));
        assertEquals(1, marked.get(second));

        assertEquals("0", getButtonForCell(first).getText());
        assertEquals("1", getButtonForCell(second).getText());
    }

    @Test
    @DisplayName("Buttons are cleared before updating text")
    void testButtonsClearedBeforeTextUpdate() {
        // Set some dummy text
        gui.getCells().keySet().forEach(button -> button.setText("X"));

        Pair<Integer, Integer> target = new Pair<>(2, 2);
        gui.handleCellClick(target);

        for (var entry : gui.getCells().entrySet()) {
            if (entry.getValue().equals(target)) {
                assertEquals("0", entry.getKey().getText());
            } else {
                assertEquals("", entry.getKey().getText());
            }
        }
    }

    @Test
    @DisplayName("Translated cell wraps correctly inside bounds")
    void testTranslatedCellWrapsInsideBounds() {
        Pair<Integer, Integer> base = new Pair<>(SIZE - 3, SIZE - 3);
        Pair<Integer, Integer> adjacent = new Pair<>(SIZE - 3, SIZE - 2);
        Pair<Integer, Integer> expected = new Pair<>(SIZE - 2, SIZE - 4);

        gui.handleCellClick(base);
        gui.handleCellClick(adjacent);

        assertTrue(controller.getMarkedCells().containsKey(expected));
        JButton translatedButton = getButtonForCell(expected);
        assertEquals("0", translatedButton.getText());
    }


    private JButton getButtonForCell(Pair<Integer, Integer> cell) {
        for (var entry : gui.getCells().entrySet()) {
            if (entry.getValue().equals(cell)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static class TestLogger implements Logger {
        @Override
        public void log(LogLevel level, String message) {
            System.out.printf("[%s] %s%n", level.name(), message);
        }
    }
}
