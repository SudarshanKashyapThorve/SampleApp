package com.au10tix.au10sample.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.au10tix.au10sample.BaseDetectionFragment;
import com.au10tix.au10sample.R;
import com.au10tix.au10sample.custom.Au10tixIQCameraVisionQualityEvents;
import com.senticore.au10tix.sdk.algorithms.ImageQuality;
import com.senticore.au10tix.sdk.cameraVision.Au10tixCameraVision;
import com.senticore.au10tix.sdk.utils.IQSufficiencyCheck;

public class CustomDocumentQualityIndicationDetectionFragment extends BaseDetectionFragment {
    private static final String TAG = "Custom Document Quality";

    TextView
            reflectionTextView,
            sharpnessTextView,
            brightnessTextView;
    boolean
            reflectTvVisible = true,
            sharpTvVisible = true,
            brightTvVisible = true;

    public static CustomDocumentQualityIndicationDetectionFragment newInstance() {
        CustomDocumentQualityIndicationDetectionFragment fragment = new CustomDocumentQualityIndicationDetectionFragment();

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_custom_quality_detector, container, false);
        reflectionTextView = root.findViewById(R.id.textViewReflection);
        sharpnessTextView = root.findViewById(R.id.textViewSharpness);
        brightnessTextView = root.findViewById(R.id.textViewBrightness);
        cameraVisionContainer = root.findViewById(R.id.cameraVisionContainer);

        return root;
    }

    private void showNotification(int indicator, final boolean shouldShow) {
        switch (indicator) {
            case ImageQuality.IC_SHARPNESS:
                if (sharpTvVisible != shouldShow) {
                    sharpTvVisible = shouldShow;

                    sharpnessTextView.setVisibility(shouldShow ? View.VISIBLE : View.INVISIBLE);
                }
                break;
            case ImageQuality.IC_BRIGHTNESS:
                if (brightTvVisible != shouldShow) {
                    brightTvVisible = shouldShow;

                    brightnessTextView.setVisibility(shouldShow ? View.VISIBLE : View.INVISIBLE);
                }
                break;
            case ImageQuality.IC_REFLECTION:
                if (reflectTvVisible != shouldShow) {
                    reflectTvVisible = shouldShow;

                    reflectionTextView.setVisibility(shouldShow ? View.VISIBLE : View.INVISIBLE);
                }
                break;
        }
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

    /**
     * This implementation permits separate quality parameter indication.
     */
    @SuppressLint("MissingPermission")
    @Override
    protected void buildCameraVision() {

        mCameraVision = (new Au10tixIQCameraVisionQualityEvents.Builder(getActivity()))
                .qualityPeriod(250L)
                .setQualityListener(result -> (new Handler(Looper.getMainLooper())).post(() -> {

                    showNotification(ImageQuality.IC_SHARPNESS | ImageQuality.IC_REFLECTION | ImageQuality.IC_BRIGHTNESS, !IQSufficiencyCheck.docID(result));
                    if (result.isUsed(ImageQuality.IC_SHARPNESS)) {
                        Log.w("quality test", "Sharpness value: " + result.getSharpness());
                        showNotification(ImageQuality.IC_SHARPNESS, !IQSufficiencyCheck.docID(ImageQuality.IC_SHARPNESS, result));
                    }
                    if (result.isUsed(ImageQuality.IC_REFLECTION)) {
                        Log.w("quality test", "Reflection value: " + result.getReflection());

                        showNotification(ImageQuality.IC_REFLECTION, !IQSufficiencyCheck.docID(ImageQuality.IC_REFLECTION, result));
                    }
                    if (result.isUsed(ImageQuality.IC_BRIGHTNESS)) {
                        Log.w("quality test", "Brightness value: " + result.getBrightness());

                        showNotification(ImageQuality.IC_BRIGHTNESS, !IQSufficiencyCheck.docID(ImageQuality.IC_BRIGHTNESS, result));
                    }

                }))
                .facingMode(Au10tixCameraVision.CAMERA_FACING_BACK)
                .detectionMode(Au10tixCameraVision.DOCUMENT_DETECTION_MODE)
                .showLiveFinder(true)
                .onBack(tixBackListener)
                .onCapture(tixCaptureListener)
                .onProcessing(tixProcessingListener)
                .build();

        cameraVisionContainer.addView(mCameraVision);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCameraVision != null) {

            mCameraVision.setTitleText(getString(R.string.tix_image_quality));
        }
    }
}
