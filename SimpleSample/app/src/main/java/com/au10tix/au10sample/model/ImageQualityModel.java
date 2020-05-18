package com.au10tix.au10sample.model;

import com.senticore.au10tix.sdk.algorithms.ImageQuality;



public class ImageQualityModel {

     double brightness;
     double saturation;
     double sharpness;
     double reflection;
     double noise;
     double contrast;

    public int getBrightness() {
        return (int)brightness;
    }

    public double getSaturation() {
        return saturation;
    }

    public double getSharpness() {
        return sharpness;
    }

    public double getReflection() {
        return reflection;
    }

    public double getNoise() {
        return noise;
    }

    public double getContrast() {
        return contrast;
    }


    /**
     * Values are simplified for the purpose of their presentation only.
     */
    public ImageQualityModel(ImageQuality.Result qualityParams) {
        brightness = Math.floor(qualityParams.getBrightness() * 100) / 100;
        saturation = Math.floor(qualityParams.getSaturation() * 100) / 100;
        sharpness = Math.floor(qualityParams.getSharpness() * 100) / 100;
        reflection = Math.floor(qualityParams.getReflection() * 100) / 100;
        noise = Math.floor(qualityParams.getNoise() * 100) / 100;
        contrast = Math.floor(qualityParams.getContrast() * 100) / 100;


    }
}
