import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

public class ImageEditorPanel extends JPanel implements KeyListener {

    Color[][] pixels;
    final int OFFSET = 1;

    public ImageEditorPanel() {
        BufferedImage imageIn = null;
        try {
            imageIn = ImageIO.read(new File("lake.jpg"));
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        pixels = makeColorArray(imageIn);
        setPreferredSize(new Dimension(pixels[0].length, pixels.length));
        setBackground(Color.BLACK);
        addKeyListener(this);
    }

    public void paintComponent(Graphics g) {
        for (int row = 0; row < pixels.length; row++) {
            for (int col = 0; col < pixels[0].length; col++) {
                g.setColor(pixels[row][col]);
                g.fillRect(col, row, 1, 1);
            }
        }
    }

    //flips image horizontally
    public Color[][] flipHoriz(Color[][] oldPixels) {
        Color[][] newPixels = new Color[pixels.length][pixels[0].length];
        for (int r = 0; r < newPixels.length; r++) {
            for (int c = 0; c < newPixels[r].length; c++) {
                newPixels[r][newPixels[r].length - OFFSET - c] = oldPixels[r][c];
            }
        }
        return newPixels;
    }

    //flips the image vertically
    public Color[][] flipVert(Color[][] oldPixels) {
        Color[][] newPixels = new Color[pixels.length][pixels[0].length];
        for (int r = 0; r < newPixels.length; r++) {
            for (int c = 0; c < newPixels[r].length; c++) {
                newPixels[newPixels.length - OFFSET - r][c] = oldPixels[r][c];
            }
        }
        return newPixels;
    }

    //makes the image grey scale
    public Color[][] makeGrey(Color[][] oldPixels) {
        Color[][] newPixels = new Color[pixels.length][pixels[0].length];
        for (int i = 0; i < oldPixels.length; i++) {
            for (int j = 0; j < oldPixels[0].length; j++) {

                int grey = (oldPixels[i][j].getRed() + oldPixels[i][j].getBlue() + oldPixels[i][j].getGreen()) / 3;
                newPixels[i][j] = new Color(grey, grey, grey);
            }
        }
        return newPixels;
    }

    //blurs the image
    public Color[][] makeBlur(Color[][] oldPixels) {
        Color[][] newPixels = new Color[pixels.length][pixels[0].length];
        int blurFactor = 10;
        for (int r = 0; r < newPixels.length; r++) {
            for (int c = 0; c < newPixels[0].length; c++) {
                int red = 0;
                int blue = 0;
                int green = 0;
                int divide = 0;
                for (int i = r - blurFactor; i < r + blurFactor; i++) {
                    for (int j = c - blurFactor; j < c + blurFactor; j++) {
                        if (i > 0 && j > 0 && i < oldPixels.length && j < oldPixels[c].length) {
                            red += oldPixels[i][j].getRed();
                            blue += oldPixels[i][j].getBlue();
                            green += oldPixels[i][j].getGreen();
                            divide++;
                        }
                    }

                }
                red /= divide;
                blue /= divide;
                green /= divide;
                newPixels[r][c] = new Color(red, green, blue);
            }

        }
        return newPixels;
    }

    //make image looks like a comic book / posterized
    public Color[][] makeComic(Color[][] oldPixels) {
        Color cream = new Color(255, 249, 143);
        Color darkBlue = new Color(8, 32, 99);
        Color lightBlue = new Color(0, 221, 255);
        Color red = new Color(255, 0, 0);
        Color[][] newPixels = new Color[pixels.length][pixels[0].length];
        for (int r = 0; r < oldPixels.length; r++) {
            for (int c = 0; c < oldPixels[0].length; c++) {
                Color singlePixel = oldPixels[r][c];
                double distanceFromRed = (Math.sqrt((Math.pow(red.getRed() - singlePixel.getRed(), 2)) + 
                    (Math.pow(red.getGreen() - singlePixel.getGreen(), 2)) + 
                    (Math.pow(red.getBlue() - singlePixel.getBlue(), 2))));
                double distanceFromDarkBlue = (Math.sqrt((Math.pow(darkBlue.getRed() - singlePixel.getRed(), 2)) + 
                    (Math.pow(darkBlue.getGreen() - singlePixel.getGreen(), 2)) + 
                    (Math.pow(darkBlue.getBlue() - singlePixel.getBlue(), 2))));
                double distanceFromLightBlue = (Math.sqrt((Math.pow(lightBlue.getRed() - singlePixel.getRed(), 2)) + 
                    (Math.pow(lightBlue.getGreen() - singlePixel.getGreen(), 2)) + 
                    (Math.pow(lightBlue.getBlue() - singlePixel.getBlue(), 2))));
                double distanceFromCream = (Math.sqrt((Math.pow(cream.getRed() - singlePixel.getRed(), 2)) + 
                    (Math.pow(cream.getGreen() - singlePixel.getGreen(), 2)) + 
                    (Math.pow(cream.getBlue() - singlePixel.getBlue(), 2))));
                double firstMin = Math.min(distanceFromRed, distanceFromLightBlue);
                double secondMin = Math.min(distanceFromCream, distanceFromDarkBlue);
                double finalMin = Math.min(firstMin, secondMin);
                if(finalMin == distanceFromCream){
                    newPixels[r][c] = cream;
                }
                if(finalMin == distanceFromDarkBlue){
                    newPixels[r][c] = darkBlue;
                }
                if(finalMin == distanceFromLightBlue){
                    newPixels[r][c] = lightBlue;
                }
                if(finalMin == distanceFromRed){
                    newPixels[r][c] = red;
                }
            }
        }
        return newPixels;
    }

    //Makes image black and white
    public Color[][] makeBlackWhite(Color[][] oldPixels){
        Color white = new Color(255, 255, 255);
        Color black = new Color(0, 0, 0);
        Color[][] newPixels = new Color[pixels.length][pixels[0].length];
        for (int r = 0; r < oldPixels.length; r++) {
            for (int c = 0; c < oldPixels[0].length; c++) {
                Color singlePixel = oldPixels[r][c];
                double distanceFromWhite = (Math.sqrt((Math.pow(white.getRed() - singlePixel.getRed(), 2)) + 
                    (Math.pow(white.getGreen() - singlePixel.getGreen(), 2)) + 
                    (Math.pow(white.getBlue() - singlePixel.getBlue(), 2))));
                double distancFromBlack = (Math.sqrt((Math.pow(black.getRed() - singlePixel.getRed(), 2)) + 
                    (Math.pow(black.getGreen() - singlePixel.getGreen(), 2)) + 
                    (Math.pow(black.getBlue() - singlePixel.getBlue(), 2))));
                double min = Math.min(distancFromBlack, distanceFromWhite);
                if(min == distancFromBlack){
                    newPixels[r][c] = black;
                }
                if(min == distanceFromWhite){
                    newPixels[r][c] = white;
                }
            }
        }
        return newPixels;
    }

    //Takes whatever value of of color is highest, and sets pixel to only that color
    public Color[][] makeMax(Color[][] oldPixels){
        Color red = new Color(255, 0, 0);
        Color green = new Color(0, 255, 0);
        Color blue = new Color(0, 0, 255);
        Color[][] newPixels = new Color[pixels.length][pixels[0].length];
        for (int r = 0; r < newPixels.length; r++) {
            for (int c = 0; c < newPixels[0].length; c++) {
                if(oldPixels[r][c].getRed() > oldPixels[r][c].getBlue() && oldPixels[r][c].getRed() > oldPixels[r][c].getGreen()){
                    newPixels[r][c] = red;
                }if(oldPixels[r][c].getBlue() > oldPixels[r][c].getGreen() && oldPixels[r][c].getBlue() > oldPixels[r][c].getRed()){
                    newPixels[r][c] = blue;
                }if(oldPixels[r][c].getGreen() > oldPixels[r][c].getBlue() && oldPixels[r][c].getGreen() > oldPixels[r][c].getRed()){
                    newPixels[r][c] = green;
                }  
            }
        }
        return newPixels;
    }

    public Color[][] makeColorArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Color[][] result = new Color[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color c = new Color(image.getRGB(col, row), true);
                result[row][col] = c;
            }
        }
        return result;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'v') {
            pixels = flipVert(pixels);
        }
        if (e.getKeyChar() == 'b') {
            pixels = makeBlur(pixels);
        }
        if (e.getKeyChar() == 'g') {
            pixels = makeGrey(pixels);
        }
        if (e.getKeyChar() == 'h') {
            pixels = flipHoriz(pixels);
        }
        if (e.getKeyChar() == 'c') {
            pixels = makeComic(pixels);
        }
        if(e.getKeyChar() == 'w') {
            pixels = makeBlackWhite(pixels);
        }
        if(e.getKeyChar() == 'm') {
            pixels = makeMax(pixels);
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //unused
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //unused
    }
}
