package gui;

import gui.logger.LogLevel;
import gui.logger.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;


class ControllerTest {

    @Spy Logger spyLogger;

    private Controller controller;
    private static final int TEST_SIZE = 8;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        controller = new ControllerImpl(TEST_SIZE, spyLogger);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Nested
    class ControllerInitialization {
        @Test
        @DisplayName("When initialized marked cells should be Empty")
        void checkInitialMarkedCells() {
            assertTrue(controller.getMarkedCells().isEmpty());
        }

        @Test
        @DisplayName("Logger is called correctly when class is initialized")
        void checkLoggerOnInitialize() {
            verify(spyLogger).log(LogLevel.INFO, "New game initialized");
        }
    }

    @Nested
    class ControllerUsage {
        @Test
        @DisplayName("Mark should add a cells correctly inside the grid")
        void addCellInsideGrid() {
            var cellToMark = new Pair<>(0,0);
            controller.mark(cellToMark);
            assertFalse(controller.getMarkedCells().isEmpty());
            assertEquals(1, controller.getMarkedCells().size());
            assertTrue(controller.getMarkedCells().containsKey(cellToMark));
            assertEquals(0, controller.getMarkedCells().get(cellToMark));
        }

        @Test
        @DisplayName("Logger should be called correctly when mark is called")
        void checkLoggerOnMarkIsCalled() {
            var cellToMark = new Pair<>(0,0);
            controller.mark(cellToMark);
            verify(spyLogger).log(LogLevel.INFO, "Marking cell " + cellToMark);
        }

        @Test
        @DisplayName("Continuously incrementing value on adding cells")
        void incrementValueOfCells() {
            var cellToMark = new Pair<>(0,0);
            controller.mark(cellToMark);
            assertEquals(1, controller.getMarkedCells().size());
            assertEquals(0, controller.getMarkedCells().get(cellToMark));
            var cellToMark2 = new Pair<>(1,2);
            controller.mark(cellToMark2);
            assertEquals(2, controller.getMarkedCells().size());
            assertEquals(1, controller.getMarkedCells().get(cellToMark2));
            var cellToMark3 = new Pair<>(3,3);
            controller.mark(cellToMark3);
            assertEquals(3, controller.getMarkedCells().size());
            assertEquals(2, controller.getMarkedCells().get(cellToMark3));
        }

        @Test
        @DisplayName("Mark a cell that is adjacent to another")
        void markCellAdjacentToAnother() {
            var cellToMark = new Pair<>(0, 0);
            controller.mark(cellToMark);
            var adjacent = new Pair<>(0, 1);
            controller.mark(adjacent);
            assertEquals(1, controller.getMarkedCells().size());
        }

        @Test
        @DisplayName("Move cells up right when in translating status")
        void moveCellsUpRight() {
            var cellToMark = new Pair<>(3, 3);
            controller.mark(cellToMark);
            var adjacent = new Pair<>(3, 4);
            controller.mark(adjacent);
            assertEquals(1, controller.getMarkedCells().size());
            assertTrue(controller.getMarkedCells().containsKey(new Pair<>(4,2)));
        }

        @Test
        @DisplayName("Clicking on an already clicked cells produce no effects")
        void clickOnSameCell() {
            var cellToMark = new Pair<>(0, 0);
            controller.mark(cellToMark);
            controller.mark(cellToMark);
            assertEquals(1, controller.getMarkedCells().size());
            assertTrue(controller.getMarkedCells().containsKey(cellToMark));
            assertFalse(controller.isOver());
        }

        @Test
        @DisplayName("Moved cells will mantain same counter value")
        void movingCellCounterVal() {
            var cellToMark = new Pair<>(3, 3);
            controller.mark(cellToMark);
            assertEquals(1, controller.getMarkedCells().size());
            assertEquals(0, controller.getMarkedCells().get(cellToMark));

            var adjacent = new Pair<>(3, 4);
            controller.mark(adjacent);

            var movedCell = new Pair<>(4, 2);
            assertEquals(1, controller.getMarkedCells().size());
            assertEquals(0, controller.getMarkedCells().get(movedCell));


        }
    }

    @Nested
    class EndGameFinalization {
        @Test
        @DisplayName("Check game end if the next move upper-right is outside the grid")
        void checkEndGame() {
            var cellToMark = new Pair<>(0, 0);
            controller.mark(cellToMark);
            var adjacent = new Pair<>(0, 1);
            controller.mark(adjacent);
            assertTrue(controller.isOver());
        }

        @Test
        @DisplayName("Can't hit if the game is over")
        void checkCantMakeAMoveIfGameEnded() {
            var cellToMark = new Pair<>(0, 0);
            controller.mark(cellToMark);
            var adjacent = new Pair<>(0, 1);
            controller.mark(adjacent);
            var anotherCellToMark = new Pair<>(1,1);
            assertThrows(IllegalStateException.class, () -> controller.mark(anotherCellToMark));
            verify(spyLogger).log(LogLevel.ERROR, "Causing error while marking" + anotherCellToMark);
        }
    }


}