package com.project.steganography;

import java.lang.reflect.Constructor;

public class SteganographyAlgorithmFactory {
    /* The path to image_steganography algorithms classes */
    private final static String PATH_TO_ALGORITHMS_PACKAGE = "com.project.steganography.";

    public static SteganographyAlgorithm createSteganographyAlgorithm(String className) throws Exception {
        String tmpClassName = PATH_TO_ALGORITHMS_PACKAGE;
        tmpClassName = tmpClassName.concat(className);
        Object algoObject;

        Class<?> c = Class.forName(tmpClassName);
        Constructor<?> constr = c.getConstructor();
        algoObject = constr.newInstance();

        return ( SteganographyAlgorithm ) algoObject;
    }
}