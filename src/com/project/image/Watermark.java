package com.project.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Watermark extends Picture {
    public Watermark(String path) {
        super(path);
    }

    public Integer[] getBlackWhitePixelsArr() {
        ArrayList<Integer> resultPixels = new ArrayList<>();
        int heightWatermark = getHeight();
        int widthWatermark = getWidth();

        Integer[] res = new Integer[heightWatermark * widthWatermark];

        for (int x = 0; x < heightWatermark; x++) {
            for (int y = 0; y < widthWatermark; y++) {
                Color pixel = new Color(getRGB(x, y));

                if (pixel.getRed() == 0 && pixel.getGreen() == 0 & pixel.getBlue() == 0) {
                    resultPixels.add(0);
                } else {
                    resultPixels.add(1);
                }
            }
        }

        return resultPixels.toArray(res);
    }

    public void recover(int[] pixelsArr) {
        int weight = getWidth();
        int height = getHeight();

        System.out.println("height " + height );
        int i = 0;

        this.bufferedImage = new BufferedImage(height, weight, Picture.BMP_IMAGE_TYPE);

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < weight; y++) {
                Color pixel = pixelsArr[i] == 0 ? new Color(0,0,0)
                        : new Color(255,255,255);

                i++;

                bufferedImage.setRGB(x,y, pixel.getRGB());
            }
        }
    }
}