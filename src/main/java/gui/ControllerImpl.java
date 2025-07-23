package gui;

import gui.logger.LogLevel;
import gui.logger.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ControllerImpl implements Controller {

    private Map<Pair<Integer, Integer>, Integer> cells  = new HashMap<>();
    private boolean moveFlag = false;
    private boolean overFlag = false;
    private final int gridSize;
    private final Logger logger;

    public ControllerImpl(int gridSize, Logger logger) {
        this.gridSize = gridSize;
        this.logger = logger;
        this.logger.log(LogLevel.INFO, "New game initialized");
    }

    @Override
    public void mark(final Pair<Integer, Integer> cell) {

        this.logger.log(LogLevel.INFO, "Marking cell " + cell);
        if(this.isOver()) {
            this.logger.log(LogLevel.ERROR, "Causing error while marking" + cell);
            throw new IllegalStateException("Cannot mark if the game is over");
        }

        boolean isAdjacent = cells.keySet().stream()
                .flatMap(this::adjacentFromCells)
                .anyMatch(cell::equals);

        if (moveFlag || isAdjacent) {
            this.moveFlag = true;
            moveCells();
            return;
        }

        this.cells.put(cell, this.cells.size());
    }

    @Override
    public boolean isOver() {
        return this.overFlag;
    }

    @Override
    public Map<Pair<Integer, Integer>, Integer> getMarkedCells() {
        return Map.copyOf(this.cells);
    }

    private void moveCells() {
        this.cells = adjustCells(cells);
        this.overFlag = this.cells.keySet().stream()
                .anyMatch(this::checkOutOfBounds);
    }

    private Stream<Pair<Integer, Integer>> adjacentFromCells(final Pair<Integer, Integer> cell) {
        return IntStream.rangeClosed(-1, 1)
                        .boxed()
                        .flatMap(x ->
                                IntStream.rangeClosed(-1, 1)
                                        .boxed()
                                        .map(y -> new Pair<>(x, y))
                                        .map(other ->
                                                new Pair<>(other.x() + cell.x(), other.y() + cell.y())
                                        )
                        )
                        .filter(p -> !(p.x() == 0 && p.y() == 0));
    }


    private Map<Pair<Integer, Integer>, Integer> adjustCells(final Map<Pair<Integer, Integer>, Integer> startingCells) {
        return startingCells.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> new Pair<>(entry.getKey().x() + 1, entry.getKey().y() - 1),
                        Map.Entry::getValue                                         // same value
                ));
    }

    private boolean checkOutOfBounds(final Pair<Integer, Integer> cell) {
        return cell.x() < 0 || cell.x() >= gridSize || cell.y() < 0 || cell.y() >= gridSize;
    }

}
