package gui;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GUI extends JFrame implements View {

    private static final long serialVersionUID = -6218820567019985015L;
    private final Map<JButton, Pair<Integer, Integer>> cells = new HashMap<>();
    private final Controller controller;


    public GUI(final int size, final Controller controller) {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(100*size, 100*size);
        this.controller = controller;

        JPanel panel = new JPanel(new GridLayout(size,size));
        this.getContentPane().add(panel);

        ActionListener al = e -> {
            var jb = (JButton) e.getSource();
            handleCellClick(this.cells.get(jb));
        };

        for (int i=0; i<size; i++){
            for (int j=0; j<size; j++){
                final JButton jb = new JButton();
                this.cells.put(jb, new Pair<>(j,i));
                jb.addActionListener(al);
                panel.add(jb);
            }
        }
    }

    @Override
    public void start() {
        this.setVisible(true);
    }

    @Override
    public void close() {
        this.dispose();
    }

    @Override
    public void handleCellClick(Pair<Integer, Integer> cell) {
        cells.keySet().forEach(button -> button.setText(""));
        this.controller.mark(cell);
        var marked = this.controller.getMarkedCells();

        cells.forEach((button, position) -> {
            Integer index = marked.get(position);
            if (index != null) {
                button.setText(String.valueOf(index));
            }
        });

        if (this.controller.isOver()) {
            this.close();
            exitApplication();
        }
    }

    @Override
    public Map<JButton, Pair<Integer, Integer>> getCells() {
        return Map.copyOf(this.cells);
    }

}