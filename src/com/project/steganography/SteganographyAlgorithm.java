package com.project.steganography;

import com.project.image.Picture;
import com.project.image.Watermark;

public abstract class SteganographyAlgorithm {

    public abstract Picture encode(Picture originalImage, Watermark watermark) throws Exception;

    public abstract Picture decode(Picture encodedImage, int X_WATERMARK, int Y_WATERMARK, int startOfSequencePSP) throws Exception;
}
