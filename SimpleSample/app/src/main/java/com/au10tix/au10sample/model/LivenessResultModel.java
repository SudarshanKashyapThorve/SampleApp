package com.au10tix.au10sample.model;

import com.senticore.au10tix.sdk.cameraVision.detectionResult.LivenessDetectionResults;

import java.io.Serializable;

public class LivenessResultModel implements Serializable {
    int binaryPattern = 0;
    int colorRange = 0;
    int opticalFlow = 0;

    public int getBinaryPattern() {
        return binaryPattern;
    }

    public int getColorRange() {
        return colorRange;
    }

    public int getOpticalFlow() {
        return opticalFlow;
    }

    public LivenessResultModel(LivenessDetectionResults.LivenessDetectionResult detectionResult){
        this.binaryPattern = detectionResult.getBinaryPattern();
        this.colorRange = detectionResult.getColorRange();
        this.opticalFlow = detectionResult.getOpticalFlow();
    }
}
