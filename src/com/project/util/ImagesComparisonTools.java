package com.project.util;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

public class ImagesComparisonTools {

    // рассчет отношения синал/шум
    public static double calculateSNR(BufferedImage im1, BufferedImage im2) {
        int width = im1.getWidth();
        int height = im1.getHeight();

        double squareImg = 0;
        double squareRecon = 0;

        int chanelNumber = 2; // blue

        Raster r1 = im1.getRaster();
        Raster r2 = im2.getRaster();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                squareRecon += Math.pow(r1.getSample(x, y, chanelNumber), 2);
                squareImg += Math.pow(r1.getSample(x, y, chanelNumber) - r2.getSample(x, y ,chanelNumber), 2);
            }
        }

        return  10.0 * logBase10(squareRecon/squareImg);
    }

    // расчет пикового отношения сигнала к шуму
    public static double calculatePSNR(BufferedImage im1, BufferedImage im2) {
        assert ( im1.getType() == im2.getType()
                && im1.getHeight() == im2.getHeight()
                && im1.getWidth() == im2.getWidth() );

        double MSE = calculateMSE( im1, im2 );

        int maxVal = 255;
        double x = Math.pow( maxVal, 2 ) / MSE;
        double PSNR = 10.0 * logBase10( x );

        return PSNR;
    }

    // рачет среднего квадрата ошибки
    public static double calculateMSE( BufferedImage im1, BufferedImage im2 ) {
        assert ( im1.getType() == im2.getType()
                && im1.getHeight() == im2.getHeight()
                && im1.getWidth() == im2.getWidth() );

        double MSE = 0;

        int width = im1.getWidth();
        int height = im1.getHeight();

        Raster r1 = im1.getRaster();
        Raster r2 = im2.getRaster();

        int chanelNumber = 2; // blue

        for( int j = 0; j < height; j++ ) {
            for( int i = 0; i < width; i++ ) {
                MSE += Math.pow(r1.getSample( i, j, chanelNumber) - r2.getSample(i, j, chanelNumber), 2 );
            }
        }

        MSE /= (width * height);

        return MSE;
    }

    // расчет нормированной среднеквадратической ошибки
    public static double calculateNMSE( BufferedImage im1, BufferedImage im2 ) {
        double MSE = calculateMSE( im1, im2 );
        return Math.sqrt( MSE );
    }

    private static double logBase10(double x ) {
        return Math.log( x ) / Math.log( 10 );
    }
}