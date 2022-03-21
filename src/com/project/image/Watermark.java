package com.project.image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Watermark extends Picture {
    public Watermark(String path) {
        super(path);
    }

    public int[] testFillContainer() {
        int height = getHeight();
        int width = getWidth();
        int[] res = new int[height * width];
        for (int i = 0; i < res.length; i++) {
            res[i] = i % 2 == 0 ? 1 : 0;
        }
        return res;
    }

    public int[] getBlackWhitePixelsArr() {
        int heightWatermark = getHeight();
        int widthWatermark = getWidth();
        int k = 0;

        int[] res = new int[heightWatermark * widthWatermark];

        for (int x = 0; x < heightWatermark; x++) {
            for (int y = 0; y < widthWatermark; y++) {
                Color pixel = new Color(getRGB(x, y));
                if (pixel.getRed() == 0 && pixel.getGreen() == 0 & pixel.getBlue() == 0) {
                    res[k] = 0;
                } else {
                    res[k] = 1;
                }
                k++;
            }
        }

        return res;
    }

    public void recover(int[] pixelsArr) {
        int width = getWidth();
        int height = getHeight();

        int i = 0;

        this.bufferedImage = new BufferedImage(width, height, Picture.BMP_IMAGE_TYPE);

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                Color pixel = pixelsArr[i] == 0 ? new Color(0,0,0)
                        : new Color(255,255,255);

                i++;

                bufferedImage.setRGB(x,y, pixel.getRGB());
            }
        }
    }
}