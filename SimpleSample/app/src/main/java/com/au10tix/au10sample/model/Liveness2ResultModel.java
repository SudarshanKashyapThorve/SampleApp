package com.au10tix.au10sample.model;

import com.senticore.au10tix.sdk.cameraVision.detectionResult.Liveness2DetectionResults;
import com.senticore.au10tix.sdk.cameraVision.detectionResult.LivenessDetectionResults;
import com.senticore.au10tix.sdk.enums.Liveness2SessionResultCode;

import java.util.ArrayList;

public class Liveness2ResultModel {
    String sessionResult;

    ArrayList<ChallengeModel> challenges = new ArrayList<>();
    LivenessResultModel livenessResult;

    public String getSessionResult() {
        return sessionResult;
    }

    public ArrayList<ChallengeModel> getChallenges() {
        return challenges;
    }

    public LivenessResultModel getLivenessResult() {
        return livenessResult;
    }

    public Liveness2ResultModel() {
    }

    public Liveness2ResultModel(Liveness2DetectionResults mResult) {

        challenges = new ArrayList<>();

        int numberOfChallenges = mResult.getResults().size();

        for (int i = 0; i < numberOfChallenges ; i++) {

            challenges.add(new ChallengeModel(mResult.getResults().get(i)));
        }
        LivenessDetectionResults.LivenessDetectionResult livenessR = new LivenessDetectionResults.LivenessDetectionResult(0,0,0);
        try {
            livenessR = mResult.getLivenessDetectionResults().getLivenessDetectionFinalResult();
        }catch (Exception e){
            e.printStackTrace();
        }


        livenessResult = new LivenessResultModel(livenessR);
        Liveness2SessionResultCode sessionRes = Liveness2SessionResultCode.Liveness2SessionResultERROR;

        try {
            sessionRes = mResult.getSessionResultCode();
        }catch (Exception e){
            e.printStackTrace();
        }

        switch (sessionRes){

            case Liveness2SessionResultFAIL:
                sessionResult = "Liveness was not detected (0)";
                break;
            case Liveness2SessionResultPASS:
                sessionResult = "Liveness detected (1)";
                break;
            case Liveness2SessionResultERROR:
                sessionResult = "Liveness detection error (2)";
                break;
            case Liveness2SessionResultLiveness2RequirementsFAIL:
                sessionResult = "Liveness2 test failed (4)";
                break;
            case Liveness2SessionResultTimeoutFAIL:
                sessionResult = "Timeout reached (5)";
                break;
            case Liveness2SessionFaceDetectionFAIL:
                sessionResult = "Face not found (6)";
                break;
            case Liveness2SessionLivenessDetectionFAIL:
                sessionResult = "Liveness test failed (7)";
                break;
            case Liveness2SessionFaceTrackingFAIL:
                sessionResult = "Face tracking lost (8)";
                break;
        }

    }
}
