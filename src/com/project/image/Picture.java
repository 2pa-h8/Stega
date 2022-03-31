package com.project.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Picture {
    final static int BMP_IMAGE_TYPE = BufferedImage.TYPE_3BYTE_BGR;
    final String IMAGE_FORMAT = "bmp";

    public final static String RED_CHANEL = "red";
    public final static String GREEN_CHANEL = "green";
    public final static String BLUE_CHANEL = "blue";

    private int height;
    private int width;

    int[][] colorComponent;

    BufferedImage bufferedImage;
    BufferedImage sourceImage;

    private File savedImage;

    public Picture(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Picture(String path) {
        try {
            sourceImage = ImageIO.read(new File(path));
            width = sourceImage.getWidth();
            height = sourceImage.getHeight();
            bufferedImage = new BufferedImage(width, height, BMP_IMAGE_TYPE);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bufferedImage.setRGB(x, y, sourceImage.getRGB(x,y));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveImage(String path) {
        try {
            savedImage = new File(path + new Random().nextInt(999) + "." + IMAGE_FORMAT);
            ImageIO.write(bufferedImage, IMAGE_FORMAT, savedImage);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void showImage() {
        try {
            Desktop.getDesktop().open(savedImage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getRGB(int x, int y) {
        return bufferedImage.getRGB(x,y);
    }

    public Color getColor(int col, int row) {
        return new Color(bufferedImage.getRGB(col, row));
    }

    public void setColor(int col, int row, Color color) {
        bufferedImage.setRGB(col, row, color.getRGB());
    }

    // получение значения пикселей в выбранном канале
    public int[][] getColorComponent(String colorComponentName) {
        this.colorComponent = new int[height][width];
        switch (colorComponentName) {
            case("red") :
                for (int x = 0; x < bufferedImage.getWidth(); x++) {
                    for (int y = 0; y < bufferedImage.getHeight(); y++) {
                        Color pixel = new Color(bufferedImage.getRGB(x, y));
                        this.colorComponent[x][y] = pixel.getRed();
                    }
                } break;
            case("green") :
                for (int x = 0; x < bufferedImage.getWidth(); x++) {
                    for (int y = 0; y < bufferedImage.getHeight(); y++) {
                        Color pixel = new Color(bufferedImage.getRGB(x, y));
                        this.colorComponent[x][y] = pixel.getGreen();
                    }
                } break;
            case("blue") :
                for (int x = 0; x < bufferedImage.getWidth(); x++) {
                    for (int y = 0; y < bufferedImage.getHeight(); y++) {
                        Color pixel = new Color(bufferedImage.getRGB(x, y));
                        this.colorComponent[x][y] = pixel.getBlue();
                    }
                } break;
        }
        return this.colorComponent;
    }

    // замена значения пикселей в выбранном канале
    public void changeColorComponent(int[][] newColorComponent, String colorComponentName) {
        switch (colorComponentName) {
            case("red") :
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        Color pixel = new Color(getRGB(x, y));
                        Color newColor = new Color(newColorComponent[x][y], pixel.getGreen(), pixel.getBlue());
                        setColor(x, y, newColor);
                    }
                } break;
            case("green") :
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        Color pixel = new Color(getRGB(x, y));
                        Color newColor = new Color(pixel.getRed(), newColorComponent[x][y], pixel.getBlue());
                        setColor(x, y, newColor);
                    }
                } break;
            case("blue") :
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        Color pixel = new Color(getRGB(x, y));
                        Color newColor = new Color(pixel.getRed(), pixel.getGreen(), newColorComponent[x][y]);
                        setColor(x, y, newColor);
                    }
                } break;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public BufferedImage getSourceImage() {
        return sourceImage;
    }

    public File getSavedImage() {
        return savedImage;
    }
}