package gui;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

class ViewTest {

    private static final int SIZE = 3;

    @Mock Controller mockController;

    private GUI gui;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        gui = new GUI(SIZE, mockController);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
        gui.dispose();
    }

    @Nested
    class GUIInteraction {

        @Test
        @DisplayName("handleCellClick should call controller.mark with correct cell")
        void testHandleCellClickCallsMark() {
            Pair<Integer, Integer> cell = new Pair<>(1, 1);
            when(mockController.getMarkedCells()).thenReturn(new HashMap<>());
            when(mockController.isOver()).thenReturn(false);

            gui.handleCellClick(cell);

            verify(mockController).mark(cell);
            verify(mockController).getMarkedCells();
            verify(mockController).isOver();
        }

        @Test
        @DisplayName("handleCellClick should update button text according to marked cells")
        void testUpdateButtonTextOnClick() {
            Pair<Integer, Integer> cell = new Pair<>(0, 0);
            Map<Pair<Integer, Integer>, Integer> marked = new HashMap<>();
            marked.put(cell, 42);

            when(mockController.getMarkedCells()).thenReturn(marked);
            when(mockController.isOver()).thenReturn(false);

            gui.handleCellClick(cell);

            JButton targetButton = getButtonForCell(cell);
            Assertions.assertNotNull(targetButton);
            Assertions.assertEquals("42", targetButton.getText());
        }

        @Test
        @DisplayName("All buttons should be cleared before update")
        void testClearButtonsBeforeUpdate() {
            gui.getCells().keySet().forEach(button -> button.setText("X"));

            Pair<Integer, Integer> cell = new Pair<>(1, 1);
            Map<Pair<Integer, Integer>, Integer> marked = new HashMap<>();
            marked.put(cell, 7);

            when(mockController.getMarkedCells()).thenReturn(marked);
            when(mockController.isOver()).thenReturn(false);

            gui.handleCellClick(cell);

            for (JButton button : gui.getCells().keySet()) {
                if (gui.getCells().get(button).equals(cell)) {
                    Assertions.assertEquals("7", button.getText());
                } else {
                    Assertions.assertEquals("", button.getText());
                }
            }
        }
    }

    @Nested
    class GUIInitialization {

        @Test
        @DisplayName("GUI initializes with SIZE*SIZE buttons")
        void testGridButtonInitialization() {
            Assertions.assertEquals(SIZE * SIZE, gui.getCells().size());
        }

        @Test
        @DisplayName("start() makes the GUI visible")
        void testStartVisibility() {
            gui.start();
            Assertions.assertTrue(gui.isVisible());
        }

        @Test
        @DisplayName("close() disposes the window")
        void testCloseDisposesWindow() {
            gui.close();
            Assertions.assertFalse(gui.isDisplayable());
        }
    }

    /**
     * Returns the JButton associated with a given cell.
     */
    private JButton getButtonForCell(Pair<Integer, Integer> cell) {
        for (var entry : gui.getCells().entrySet()) {
            if (entry.getValue().equals(cell)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
