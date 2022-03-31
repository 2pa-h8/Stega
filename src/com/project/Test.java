package com.project;

import com.project.image.Picture;
import com.project.image.Watermark;
import com.project.steganography.SpreadSpectrum;
import com.project.util.ImagesComparisonTools;

import java.awt.image.BufferedImage;

public class Test {
    public static void main(String[] args) throws Exception {
        final String SAVED_LOCATION = "C:\\Users\\user\\Desktop\\Учеба\\Стеганография\\Курсовая\\";

        Picture originalImage = new Picture("C:\\Users\\user\\Desktop\\Учеба\\Стеганография\\Курсовая\\bmp.bmp");
        Watermark watermark = new Watermark("C:\\Users\\user\\Desktop\\Учеба\\Стеганография\\Курсовая\\qrcode.bmp");

//        ********************************** ВСТРАИВАНИЕ **********************************

        SpreadSpectrum spd = new SpreadSpectrum();
        Picture resultEncode = spd.encode(originalImage, watermark, 545);
        resultEncode.saveImage(SAVED_LOCATION);

//        *********************************************************************************


//        ********************************** ИЗВЛЕЧЕНИЕ **********************************

        Watermark w = spd.decode(resultEncode, 32, 32, 545);
        w.saveImage(SAVED_LOCATION);
        w.showImage();

//        ***********************************************************************************


//        ********************************** РАСЧЕТ МЕТРИК **********************************

        BufferedImage sourceImage = originalImage.getBufferedImage();
        BufferedImage resultImage = originalImage.getSourceImage();

        System.out.println("MSE " + ImagesComparisonTools.calculateMSE(sourceImage, resultImage));
        System.out.println("NMSE " + ImagesComparisonTools.calculateNMSE(sourceImage, resultImage) + " %");
        System.out.println("SNR " + ImagesComparisonTools.calculateSNR(sourceImage, resultImage) + " дБ");
        System.out.println("PSNR " + ImagesComparisonTools.calculatePSNR(sourceImage, resultImage) + " дБ");


//        ***********************************************************************************
    }
}