package com.au10tix.au10sample.model;

import com.senticore.au10tix.sdk.cameraVision.detectionResult.Liveness2DetectionResults;

public class ChallengeModel {

    public String getChallengeType() {
        return challengeType;
    }

    public boolean isChallengePassed() {
        return challengePassed;
    }

    public boolean isFaceTrackingMaintained() {
        return faceTrackingMaintained;
    }

    String challengeType;
    boolean challengePassed = false, faceTrackingMaintained = true;
    double challengeDuration = 0D;

    public ChallengeModel(Liveness2DetectionResults.Liveness2DetectionResult singleChallenge) {

        switch (singleChallenge.getGestureType()){

            case GestureTypeFaceForward:
                challengeType = "Face Forward";
                break;
            case GestureTypePanLeft:
                challengeType = "Turn Left";
                break;
            case GestureTypePanRight:
                challengeType = "Turn Right";
                break;
            case GestureTypeSmile:
                challengeType = "Smile";
                break;
            case GestureTypeEyesClosed:
                challengeType = "Close Eyes";
                break;
        }

        if(singleChallenge.getResultCode() == 1){
            challengePassed = true;
        }

        challengeDuration = singleChallenge.getDetectionTimeDouble();
        faceTrackingMaintained = singleChallenge.wasFaceTracked();

    }
}
