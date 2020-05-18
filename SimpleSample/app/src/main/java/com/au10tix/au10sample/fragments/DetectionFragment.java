package com.au10tix.au10sample.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.au10tix.au10sample.BaseDetectionFragment;
import com.au10tix.au10sample.R;
import com.senticore.au10tix.sdk.cameraVision.Au10tixIQCameraVision;
import com.senticore.au10tix.sdk.cameraVision.CameraVision;

/**
 * This example presents a default implementation.
 */
public class DetectionFragment extends BaseDetectionFragment {

    private static final String TAG = "Detection Fragment";
    private static final String ARG_DETECTION_TYPE = "detectionType";
    private int mDetectionType;
    private int mFacing;
    private String mTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDetectionType = getArguments().getInt(ARG_DETECTION_TYPE, 0);
            switch (mDetectionType) {
                case FACE_DETECTION_MODE:
                    mTitle = getResources().getStringArray(R.array.detectionOptions)[FACE_DETECTION_MODE];

                case LIVENESS_DETECTION_MODE:
                    mFacing = CameraVision.CAMERA_FACING_FRONT;
                    break;

                case DOCUMENT_DETECTION_MODE:
                    mTitle = getResources().getStringArray(R.array.detectionOptions)[DOCUMENT_DETECTION_MODE];
                    mFacing = CameraVision.CAMERA_FACING_BACK;
                    break;

                case BARCODE_DETECTION_MODE:
                    mTitle = getResources().getStringArray(R.array.detectionOptions)[BARCODE_DETECTION_MODE];
                    mFacing = CameraVision.CAMERA_FACING_BACK;
                    break;
            }
        }
    }

    @SuppressLint("MissingPermission")
    protected void buildCameraVision() {

        mCameraVision = (new Au10tixIQCameraVision.Builder(getActivity()))
                .qualityPeriod(250L)
                .customQualityToast(R.layout.view_quality_dialog)
                .showLiveFinder(true)
                .onBack(tixBackListener)
                .onCapture(tixCaptureListener)
                .facingMode(mFacing)
                .detectionMode(mDetectionType)
                .build();

        cameraVisionContainer.addView(mCameraVision);

        mCameraVision.setTitleText(mTitle);
        mCameraVision.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detector, container, false);
        cameraVisionContainer = root.findViewById(R.id.cameraVisionContainer);

        return root;
    }

    @Override
    public void onStop() {
        super.onStop();

        cameraVisionContainer.removeAllViews();
    }

}
