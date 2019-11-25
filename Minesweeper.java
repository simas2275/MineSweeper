package zaidimas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.plaf.metal.MetalButtonUI;

public class Minesweeper implements ActionListener {

    JFrame frame = new JFrame("Mincesweeper");
    JButton reset = new JButton("Reset");
    JButton[][] buttons = new JButton[20][20];
    Container grid = new Container();

    int[][] count = new int[20][20];
    int minesAmount = 100;
    int MINE = 10;

    public Minesweeper() {
        frame.setSize(1000, 1000);
        frame.setLayout(new BorderLayout());
        frame.add(reset, BorderLayout.NORTH);
        reset.addActionListener(this);
        setGrid();
        randomMines();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void setGrid() {
        grid.setLayout(new GridLayout(20, 20));
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons.length; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].addActionListener(this);
                grid.add(buttons[i][j]);
            }
        }
        frame.add(grid, BorderLayout.CENTER);
    }

    public void randomMines() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int x = 0; x < count.length; x++) {
            for (int y = 0; y < count.length; y++) {
                list.add(x * 100 + y);
            }
        }
        count = new int[20][20];
        for (int i = 0; i < minesAmount; i++) {
            int pasirinkimas = (int) (Math.random() * list.size());
            count[list.get(pasirinkimas) / 100][list.get(pasirinkimas) % 100] = MINE;
            System.out.println(list.get(pasirinkimas));
            list.remove(pasirinkimas);
        }
        minesNears();
    }

    public void minesNears() {
        for (int x = 0; x < count.length; x++) {
            for (int y = 0; y < count.length; y++) {
                if (count[x][y] != MINE) {
                    int neighbourCount = 0;
                    if (x > 0 && y > 0 && count[x - 1][y - 1] == MINE) { // virsutinis kaire
                        neighbourCount++;
                    }
                    if (y > 0 && count[x][y - 1] == MINE) { // virsus 
                        neighbourCount++;
                    }
                    if (y > 0 && x < count.length - 1 && count[x + 1][y - 1] == MINE) { // virsus desine
                        neighbourCount++;
                    }
                    if (x < count.length - 1 && count[x + 1][y] == MINE) { // desine
                        neighbourCount++;
                    }
                    if (x < count.length - 1 && y < count.length - 1 && count[x + 1][y + 1] == MINE) { // apacia desine
                        neighbourCount++;
                    }
                    if (y < count.length - 1 && count[x][y + 1] == MINE) { // apacia
                        neighbourCount++;
                    }
                    if (x > 0 && y < count.length - 1 && count[x - 1][y + 1] == MINE) { //  apacia kaire
                        neighbourCount++;
                    }
                    if (x > 0 && count[x - 1][y] == MINE) { // kaire
                        neighbourCount++;
                    }
                    count[x][y] = neighbourCount;

                }

                if (count[x][y] == 1) {
                    buttons[x][y].setUI(new MetalButtonUI() {
                        protected Color getDisabledTextColor() {
                            return Color.ORANGE;
                        }
                    });
                } else if (count[x][y] == 2 || count[x][y] == 5) {
                    buttons[x][y].setUI(new MetalButtonUI() {
                        protected Color getDisabledTextColor() {
                            return Color.green;
                        }
                    });
                } else if (count[x][y] == 3 || count[x][y] == 6) {
                    buttons[x][y].setUI(new MetalButtonUI() {
                        protected Color getDisabledTextColor() {
                            return Color.red;
                        }
                    });
                } else if (count[x][y] == 4 || count[x][y] == 7) {
                    buttons[x][y].setUI(new MetalButtonUI() {
                        protected Color getDisabledTextColor() {
                            return Color.blue;
                        }
                    });
                }

            }

        }
    }

    public void lose() {
        for (int x = 0; x < buttons.length; x++) {
            for (int y = 0; y < buttons.length; y++) {
                if (buttons[x][y].isEnabled()) {
                    if (count[x][y] != MINE) {
                        buttons[x][y].setText(count[x][y] + "");
                        buttons[x][y].setEnabled(false);
                    } else {
                        buttons[x][y].setUI(new MetalButtonUI() {
                            protected Color getDisabledTextColor() {
                                return Color.black;
                            }
                        });
                        buttons[x][y].setText("X");
                        buttons[x][y].setEnabled(false);

                    }
                }
            }
        }
        JOptionPane.showMessageDialog(frame, "You lose");
    }

    public void nearZeros(ArrayList<Integer> toClear) {
        if (!toClear.isEmpty()) {
            int x = toClear.get(0) / 100;
            int y = toClear.get(0) % 100;
            System.out.println(x + "x");
            System.out.println(y + "y");
            toClear.remove(0);
            if (count[x][y] == 0) {
                if (x > 0 && y > 0 && buttons[x - 1][y - 1].isEnabled()) { // virsus kaire
                    buttons[x - 1][y - 1].setText(count[x - 1][y - 1] + "");
                    buttons[x - 1][y - 1].setEnabled(false);
                    if (count[x - 1][y - 1] == 0) {
                        toClear.add((x - 1) * 100 + (y - 1));
                    }
                }
                if (y > 0 && buttons[x][y - 1].isEnabled()) { // virsus
                    buttons[x][y - 1].setText(count[x][y - 1] + "");
                    buttons[x][y - 1].setEnabled(false);
                    if (count[x][y - 1] == 0) {
                        toClear.add(x * 100 + (y - 1));
                    }
                }
                if (y > 0 && x < count.length - 1 && buttons[x + 1][y - 1].isEnabled()) { // virsus desine
                    buttons[x + 1][y - 1].setText(count[x + 1][y - 1] + "");

                    buttons[x + 1][y - 1].setEnabled(false);
                    if (count[x + 1][y - 1] == 0) {
                        toClear.add((x + 1) * 100 + (y - 1));
                    }
                }
                if (x > 0 && buttons[x - 1][y].isEnabled()) { // kaire
                    buttons[x - 1][y].setText(count[x - 1][y] + "");
                    buttons[x - 1][y].setEnabled(false);
                    if (count[x - 1][y] == 0) {
                        toClear.add((x - 1) * 100 + y);
                    }
                }
                if (x < count.length - 1 && buttons[x + 1][y].isEnabled()) { // desine
                    buttons[x + 1][y].setText(count[x + 1][y] + "");
                    buttons[x + 1][y].setEnabled(false);
                    if (count[x + 1][y] == 0) {
                        toClear.add((x + 1) * 100 + y);
                    }
                }
                if (x > 0 && y < count.length - 1 && buttons[x - 1][y + 1].isEnabled()) { // apacia kaire
                    buttons[x - 1][y + 1].setText(count[x - 1][y + 1] + "");
                    buttons[x - 1][y + 1].setEnabled(false);
                    if (count[x - 1][y + 1] == 0) {
                        toClear.add((x - 1) * 100 + (y + 1));
                    }
                }
                if (y < count.length - 1) { // apacia
                    buttons[x][y + 1].setText(count[x][y + 1] + "");
                    buttons[x][y + 1].setEnabled(false);
                    if (count[x][y + 1] == 0) {
                        toClear.add(x * 100 + (y + 1));
                    }
                }
                if (x < count.length - 1 && y < count.length - 1 && buttons[x + 1][y + 1].isEnabled()) { // apacia desine
                    buttons[x + 1][y + 1].setText(count[x + 1][y + 1] + "");
                    buttons[x + 1][y + 1].setEnabled(false);
                    if (count[x + 1][y + 1] == 0) {
                        toClear.add((x + 1) * 100 + (y + 1));
                    }
                }
            }
            nearZeros(toClear);
        }
    }

    public void checkWin() {
        boolean win = true;
        for (int x = 0; x < count.length; x++) {
            for (int y = 0; y < count.length; y++) {
                if (count[x][y] != MINE && buttons[x][y].isEnabled() == true) {
                    win = false;
                }
            }
        }
        if (win == true) {
            JOptionPane.showMessageDialog(frame, "You win");
        }
    }

    public void resetBoard() {
        for (int x = 0; x < buttons.length; x++) {
            for (int y = 0; y < buttons.length; y++) {
                buttons[x][y].setEnabled(true);
                buttons[x][y].setText("");
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(reset)) {
            resetBoard();
            randomMines();
        } else {
            for (int x = 0; x < buttons.length; x++) {
                for (int y = 0; y < buttons.length; y++) {
                    if (e.getSource().equals(buttons[x][y])) {
                        if (count[x][y] == MINE) {
                            lose();
                        } else if (count[x][y] == 0) {
                            buttons[x][y].setText(count[x][y] + "");
                            buttons[x][y].setEnabled(false);
                            ArrayList<Integer> toClear = new ArrayList<Integer>();
                            toClear.add(x * 100 + y);
                            nearZeros(toClear);
                            checkWin();
                        } else {
                            buttons[x][y].setText(count[x][y] + "");
                            buttons[x][y].setEnabled(false);
                            checkWin();
                        }

                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Minesweeper minesweeper = new Minesweeper();
    }
}
