package com.project.image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Watermark extends Picture {
    public Watermark(int width, int height) {
        super(height, width);
    }
    public Watermark(String path) {
        super(path);
    }

    public int[] getBlackWhitePixelsArr() {
        int heightWatermark = getHeight();
        int widthWatermark = getWidth();
        int k = 0;

        int[] blackWhitePixelsArr = new int[heightWatermark * widthWatermark];

        for (int x = 0; x < heightWatermark; x++) {
            for (int y = 0; y < widthWatermark; y++) {
                Color pixel = new Color(getRGB(x, y));
                if (pixel.getRed() == 0 && pixel.getGreen() == 0 && pixel.getBlue() == 0) {
                    blackWhitePixelsArr[k] = 0;
                } else {
                    blackWhitePixelsArr[k] = 1;
                }
                k++;
            }
        }

        return blackWhitePixelsArr;
    }

    public void recover(int[] pixelsArr) {
        int width = getWidth();
        int height = getHeight();

        int k = 0;

        this.bufferedImage = new BufferedImage(width, height, Picture.BMP_IMAGE_TYPE);

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                Color pixel = pixelsArr[k] == 0 ? new Color(0,0,0)
                        : new Color(255,255,255);

                k++;

                bufferedImage.setRGB(x,y, pixel.getRGB());
            }
        }
    }
}