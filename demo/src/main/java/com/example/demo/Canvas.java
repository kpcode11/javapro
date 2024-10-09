package com.example.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Canvas implements MouseMotionListener {
    JFrame frame = new JFrame();
    CanvasPanel canvasPanel;
    JPanel toolBar;
    JTextField col, sz;
    boolean drawCircle = true; // to toggle between drawing circle and rectangle
    List<ShapeAction> actions = new ArrayList<>(); // to store drawn shapes

    Canvas() {
        canvasPanel = new CanvasPanel();
        canvasPanel.addMouseMotionListener(this);
        canvasPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                drawShape(e.getX(), e.getY());
            }
        });
    }

    public void CanvasView() {
        //-----------CANVAS-------------
        canvasPanel.setBounds(320, 30, 850, 600);
        canvasPanel.setBackground(Color.white);
        frame.add(canvasPanel);
        //-----------------------------

        //-----------TOOLBAR-------------
        toolBar = new JPanel();
        toolBar.setBounds(30, 30, 250, 600);
        toolBar.setBackground(Color.white);
        toolBar.setLayout(null);
        frame.add(toolBar);
        addButton(32, 30, "#000000");
        addButton(105, 30, "#FFFFFF");
        addButton(177, 30, "#808080");
        addButton(32, 90, "#FF0000");
        addButton(105, 90, "#00FF00");
        addButton(177, 90, "#0000FF");
        addButton(32, 150, "#FFFF00");
        addButton(105, 150, "#FFA500");
        addButton(177, 150, "#A020F0");
        addButton(32, 210, "#FFC0CB");
        addButton(105, 210, "#964B00");
        addButton(177, 210, "#C32148");

        //---------CUSTOM---------------
        JLabel cllabel = new JLabel("Custom Colors : ");
        cllabel.setBounds(30, 260, 100, 50);
        toolBar.add(cllabel);
        col = new JTextField();
        col.setBounds(30, 310, 190, 30);
        col.setText("#000000");
        toolBar.add(col);

        //---------SIZE------------------
        JLabel szlabel = new JLabel("Size : ");
        szlabel.setBounds(30, 380, 100, 50);
        toolBar.add(szlabel);
        JButton sub = new JButton("-");
        sub.setBounds(30, 415, 50, 30);
        toolBar.add(sub);
        sz = new JTextField();
        sz.setBounds(100, 415, 50, 30);
        sz.setText("5");
        toolBar.add(sz);
        JButton add = new JButton("+");
        add.setBounds(170, 415, 50, 30);
        toolBar.add(add);

        sub.addActionListener(e -> {
            int currentSize = Integer.parseInt(sz.getText());
            if (currentSize > 1) {
                sz.setText(Integer.toString(currentSize - 1));
            }
        });
        add.addActionListener(e -> sz.setText(Integer.parseInt(sz.getText()) + 1 + ""));

        //---------BRUSH SHAPE TOGGLE------------------
        JButton shapeToggle = new JButton("TOGGLE SHAPE");
        shapeToggle.setBounds(30, 465, 190, 30);
        toolBar.add(shapeToggle);
        shapeToggle.addActionListener(e -> drawCircle = !drawCircle); // Toggle between circle and rectangle

        //-----------UNDO--------------
        JButton undo = new JButton("UNDO");
        undo.setBounds(30, 515, 190, 30);
        toolBar.add(undo);
        undo.addActionListener(e -> {
            if (!actions.isEmpty()) {
                actions.remove(actions.size() - 1);
                canvasPanel.repaint(); // Clear and redraw
            }
        });

        //-----------SAVE---------------
        JButton save = new JButton("SAVE");
        save.setBounds(30, 555, 190, 30);
        toolBar.add(save);
        save.addActionListener(e -> {
            try {
                BufferedImage image = canvasPanel.getImg();
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    ImageIO.write(image, "png", file);
                }
            } catch (Exception ex) {
                ex.printStackTrace(); // Debugging
            }
        });

        //------FRAME------------------
        frame.setSize(1250, 700);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.getContentPane().setBackground(Color.decode("#001122"));
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //---------------------------------
    }

    //---------MOUSE FUNCTIONS---------------------
    @Override
    public void mouseDragged(MouseEvent e) {
        drawShape(e.getX(), e.getY());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Not used
    }

    //-------------------------------------------------
    public void addButton(int x, int y, String clr) {
        JButton btn = new JButton();
        btn.setBounds(x, y, 40, 40);
        btn.setBackground(Color.decode(clr));
        btn.addActionListener(e -> col.setText(clr));
        toolBar.add(btn);
    }

    //----------DRAW SHAPES----------------------------
    public void drawShape(int x, int y) {
        String colorCode = col.getText();
        int size = Integer.parseInt(sz.getText());
        ShapeAction action = new ShapeAction(x, y, size, colorCode, drawCircle);
        actions.add(action);
        canvasPanel.repaint(); // Redraw all shapes
    }

    //----------INNER CLASS FOR UNDO FEATURE------------------
    class ShapeAction {
        int x, y, size;
        String colorCode;
        boolean isCircle;

        ShapeAction(int x, int y, int size, String colorCode, boolean isCircle) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.colorCode = colorCode;
            this.isCircle = isCircle;
        }
    }

    //---------CUSTOM PANEL CLASS FOR DRAWING--------------
    class CanvasPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (ShapeAction action : actions) {
                g.setColor(Color.decode(action.colorCode));
                if (action.isCircle) {
                    g.fillOval(action.x, action.y, action.size, action.size);
                } else {
                    g.fillRect(action.x, action.y, action.size, action.size);
                }
            }
        }

        //----------FUNCTION TO CREATE IMAGE TO SAVE-----------------------------
        public BufferedImage getImg() throws Exception {
            BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            paint(image.getGraphics());
            return image;
        }
    }
    public static void main(String[] args) {
        Canvas canvas = new Canvas();
        canvas.CanvasView();  // Start the canvas UI
    }

}