package com.au10tix.au10sample.custom;

import android.content.Context;
import android.support.annotation.NonNull;

import com.senticore.au10tix.sdk.cameraVision.Au10tixCameraVision;
import com.senticore.au10tix.sdk.cameraVision.detector.Detector;
import com.senticore.au10tix.sdk.cameraVision.detector.QualityDetector;

import java.util.List;

/**
 * This implementation provides a quality listener, for custom quality result indication.
 */
public class Au10tixIQCameraVisionQualityEvents extends Au10tixCameraVision {
    static QualityDetector mQualityDetector;
    private boolean mUseQualityDetection;

    private Au10tixIQCameraVisionQualityEvents(Context context) {
        super(context);
        this.mUseQualityDetection = true;
        this.mQualityDetector = new QualityDetector();

    }

    @NonNull
    protected List<Detector> createDetector() {
        List<Detector> detectors = super.createDetector();
        if (this.mDetectionMode != 1) {
            return detectors;
        } else {
            if (this.mUseQualityDetection) {
                detectors.add(0, this.mQualityDetector);
            }
            return detectors;
        }
    }


    public static class Builder extends com.senticore.au10tix.sdk.cameraVision.Au10tixCameraVision.Builder {
        public Builder(Context context) {
            super(context);
            this.mCameraVision = new Au10tixIQCameraVisionQualityEvents(context);
        }


        @NonNull
        public Au10tixIQCameraVisionQualityEvents.Builder qualityPeriod(long period) {
            ((Au10tixIQCameraVisionQualityEvents) this.mCameraVision).mQualityDetector.setPeriod(period);
            return this;
        }


        @NonNull
        public Au10tixIQCameraVisionQualityEvents.Builder setQualityListener(@NonNull QualityDetector.OnQualityDetectedListener qListener) {
            mQualityDetector.setOnQualityDetectedListener(qListener);
            return this;
        }
    }
}
