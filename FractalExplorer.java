/*

MandelBrot Set Fractal Explorer in JAVA
@author Aman Kumar Chauhan | amankch2023@gmail.com | amankch.tech

*/


import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

public class FractalExplorer extends JFrame {

    static final int WIDTH = 600;
    static final int HEIGHT = 600;

    Canvas canvas;
    BufferedImage fractalImage;

    static final int MAX_ITER = 200;

    static final double DEFAULT_ZOOM = 100.0;
    static final double DEFAULT_TOP_LEFT_X = -3.0;
    static final double DEFAULT_TOP_LEFT_Y = +3.0;

    double zoomFactor = DEFAULT_ZOOM;
    double topLeftX = DEFAULT_TOP_LEFT_X;
    double topLeftY = DEFAULT_TOP_LEFT_Y;

    // -------------------------------------------------------------------
    public FractalExplorer() {
        setInitialGUIProperties();
        addCanvas();
        canvas.addKeyStrokeEvents();
        updateFractal();

    }

    // -------------------------------------------------------------------

    public static void main(String[] args) {
        new FractalExplorer();
    }

    // -------------------------------------------------------------------

    private void addCanvas() {

        canvas = new Canvas();
        fractalImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        canvas.setVisible(true);
        this.add(canvas, BorderLayout.CENTER);

    } // addCanvas

    // -------------------------------------------------------------------

    private void setInitialGUIProperties() {

        this.setTitle("Fractal Explorer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(WIDTH, HEIGHT);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

    } // setInitialGUIProperties

    // -------------------------------------------------------------------
    private double getXPos(double x) {
        return x / zoomFactor + topLeftX;
    } // getXPos
      // -------------------------------------------------------------------

    private double getYPos(double y) {
        return y / zoomFactor - topLeftY;
    } // getYPos
      // -------------------------------------------------------------------

    /**
     * Updates the fractal by computing the number of iterations
     * for each point in the fractal and changing the color
     * based on that.
     **/

    public void updateFractal() {

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {

                double c_r = getXPos(x);
                double c_i = getYPos(y);

                int iterCount = computeIterations(c_r, c_i);

                int pixelColor = makeColor(iterCount);
                fractalImage.setRGB(x, y, pixelColor);

            }
        }

        canvas.repaint();

    } // updateFractal
      // -------------------------------------------------------------------

    private int makeColor(int iterCount) {

        /*
         * (r,g,b)
         * (0-255, 0-255, 0-255)
         * 00000000 00000000 00000000
         */

        int color = 0b011011100001100101101000; // 0b011011100001100101101000
        int mask = 0b000000000000010101110111; // 0b000000000000010101110111
        int shiftMag = iterCount / 13;

        if (iterCount == MAX_ITER) {
            return 0;// Color.BLACK.getRGB();
        }

        return color | (mask << shiftMag);

    } // makeColor

    // -------------------------------------------------------------------

    private int computeIterations(double c_r, double c_i) {

        /*
         * 
         * Let c = c_r + c_i
         * Let z = z_r + z_i
         * 
         * z' = z*z + c
         * = (z_r + z_i)(z_r + z_i) + c_r + c_i
         * = z_r² + 2*z_r*z_i - z_i² + c_r + c_i
         * 
         * z_r' = z_r² - z_i² + c_r
         * z_i' = 2*z_i*z_r + c_i
         * 
         */

        double z_r = 0.0;
        double z_i = 0.0;

        int iterCount = 0;

        // Modulus (distance) formula:
        // √(a² + b²) <= 2.0
        // a² + b² <= 4.0
        while (z_r * z_r + z_i * z_i <= 4.0) {

            double z_r_tmp = z_r;

            z_r = z_r * z_r - z_i * z_i + c_r;
            z_i = 2 * z_i * z_r_tmp + c_i;

            // Point was inside the Mandelbrot set
            if (iterCount >= MAX_ITER)
                return MAX_ITER;

            iterCount++;

        }

        // Complex point was outside Mandelbrot set
        return iterCount;

    } // computeIterations

    private void moveUp() {

        double curHeight = HEIGHT / zoomFactor;
        topLeftY += curHeight / 4;
        updateFractal();

    }

    private void moveDown() {

        double curHeight = HEIGHT / zoomFactor;
        topLeftY -= curHeight / 4;
        updateFractal();

    }

    private void moveLeft() {

        double curWidth = WIDTH / zoomFactor;
        topLeftX -= curWidth / 4;
        updateFractal();

    }

    private void moveRight() {

        double curWidth = WIDTH / zoomFactor;
        topLeftX += curWidth / 4;
        updateFractal();

    }

    // -------------------------------------------------------------------

    private void adjustZoom(double newX, double newY, double newZoomFactor) {

        topLeftX += newX / zoomFactor;
        topLeftY -= newY / zoomFactor;

        zoomFactor = newZoomFactor;

        topLeftX -= (WIDTH / 2) / zoomFactor;
        topLeftY += (HEIGHT / 2) / zoomFactor;

        updateFractal();

    }

    // -------------------------------------------------------------------

    private class Canvas extends JPanel implements MouseListener {

        public Canvas() {
            addMouseListener(this);
        }

        public void addKeyStrokeEvents() {

            KeyStroke wKey = KeyStroke.getKeyStroke(KeyEvent.VK_W, 0);
            KeyStroke aKey = KeyStroke.getKeyStroke(KeyEvent.VK_A, 0);
            KeyStroke sKey = KeyStroke.getKeyStroke(KeyEvent.VK_S, 0);
            KeyStroke dKey = KeyStroke.getKeyStroke(KeyEvent.VK_D, 0);

            Action wPressed = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moveUp();
                }

            };

            Action aPressed = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moveLeft();
                }
            };

            Action sPressed = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moveDown();
                }
            };

            Action dPressed = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moveRight();
                }
            };

            this.getInputMap().put(wKey, "w_key");
            this.getInputMap().put(aKey, "a_key");
            this.getInputMap().put(sKey, "s_key");
            this.getInputMap().put(dKey, "d_key");

            this.getActionMap().put("w_key", wPressed);
            this.getActionMap().put("a_key", aPressed);
            this.getActionMap().put("s_key", sPressed);
            this.getActionMap().put("d_key", dPressed);

        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(WIDTH, HEIGHT);
        } // getPreferredSize

        @Override
        public void paintComponent(Graphics drawingObj) {
            drawingObj.drawImage(fractalImage, 0, 0, null);
        } // paintComponent

        @Override
        public void mousePressed(MouseEvent mouse) {

            double x = (double) mouse.getX();
            double y = (double) mouse.getY();

            switch (mouse.getButton()) {

                // Left
                case MouseEvent.BUTTON1:
                    adjustZoom(x, y, zoomFactor * 2);
                    break;

                // Right
                case MouseEvent.BUTTON3:
                    adjustZoom(x, y, zoomFactor / 2);
                    break;

            }

        }

        @Override
        public void mouseReleased(MouseEvent mouse) {
        }

        @Override
        public void mouseClicked(MouseEvent mouse) {
        }

        @Override
        public void mouseEntered(MouseEvent mouse) {
        }

        @Override
        public void mouseExited(MouseEvent mouse) {
        }

    }
}
