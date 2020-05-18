package com.au10tix.au10sample.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.au10tix.au10sample.BaseDetectionFragment;
import com.au10tix.au10sample.R;
import com.senticore.au10tix.sdk.cameraVision.Au10tixCameraVision;

public class CustomControlsFaceDetectionFragment extends BaseDetectionFragment {

    private static final String TAG = "Custom Controls";
    View root;

    public static CustomControlsFaceDetectionFragment newInstance() {

        CustomControlsFaceDetectionFragment fragment = new CustomControlsFaceDetectionFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_custom_controls_detector, container, false);
        cameraVisionContainer = root.findViewById(R.id.cameraVisionContainer);

        return root;
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (mCameraVision != null) {
                mCameraVision.release();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        cameraVisionContainer.removeAllViews();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void buildCameraVision() {

        mCameraVision = (new Au10tixCameraVision.Builder(getActivity()))
                .showLiveFinder(true)
                .onBack(tixBackListener)
                .onCapture(tixCaptureListener)
                .detectionMode(FACE_DETECTION_MODE)
                .facingMode(Au10tixCameraVision.CAMERA_FACING_FRONT)
                .controlView(root).build();

        cameraVisionContainer.addView(mCameraVision);

        mCameraVision.start();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mCameraVision != null) {
            mCameraVision.setTitleText(getString(R.string.tix_custom_controls));
        }
    }
}
