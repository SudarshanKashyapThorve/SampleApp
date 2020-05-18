package com.au10tix.au10sample.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.au10tix.au10sample.BaseDetectionFragment;
import com.au10tix.au10sample.R;
import com.senticore.au10tix.sdk.cameraVision.Au10tixCameraVision;
import com.senticore.au10tix.sdk.cameraVision.detectionResult.DetectionResult;
import com.senticore.au10tix.sdk.enums.GestureType;
import com.senticore.au10tix.sdk.enums.Liveness2DetectorResultCode;
import com.senticore.au10tix.sdk.enums.Liveness2SessionResultCode;

public class Liveness2DetectionFragment extends BaseDetectionFragment implements Au10tixCameraVision.Liveness2SessionListener {
    private static final String TAG = "Liveness2 Detection";

    Vibrator vib;
    MediaPlayer mpF, mpS;
    int challengeCounter = 0;

    public static Liveness2DetectionFragment newInstance() {
        Liveness2DetectionFragment fragment = new Liveness2DetectionFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vib = (Vibrator) getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detector, container, false);
        cameraVisionContainer = root.findViewById(R.id.cameraVisionContainer);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mpS = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.success);
        mpF = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.fail);
    }

    @Override
    public void onStop() {
        super.onStop();

        try {
            if (mpF != null) {
                mpF.release();
            }
            if (mpS != null) {
                mpS.release();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }

        cameraVisionContainer.removeAllViews();
    }
//


    @SuppressLint("MissingPermission")
    @Override
    protected void buildCameraVision() {

        Au10tixCameraVision cameraVision = (new Au10tixCameraVision.Builder(getActivity().getApplicationContext()))
                .facingMode(Au10tixCameraVision.CAMERA_FACING_FRONT)
                .detectionMode(Au10tixCameraVision.LIVENESS2_DETECTION_MODE)
                .onBack(tixBackListener)
                .onCapture(tixCaptureListener)
                .onLiveness2SessionEvents(this)
                .build();

        mCameraVision = cameraVision;
        cameraVisionContainer.addView(mCameraVision);
    }

    /**
     * Indicate single challenge resolution.
     **/
    @Override
    public void onChallengeState(Liveness2DetectorResultCode liveness2DetectorResultCode) {

        switch (liveness2DetectorResultCode) {

            case Liveness2DetectorResultFAIL:
                challengeCounter++;

                if (vib.hasVibrator()) {

                    long[] pattern = {0, 20, 20, 10};

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vib.vibrate(VibrationEffect.createWaveform(pattern, -1));
                    } else {
                        vib.vibrate(pattern, -1);
                    }
                }

                mpF.start();
                break;
            case Liveness2DetectorResultPASS:

                if (vib.hasVibrator()) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vib.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vib.vibrate(20);
                    }
                }
                mpS.start();
                break;
        }
    }

    @Override
    protected void onDetectionResult(DetectionResult result) {
        super.onDetectionResult(result);
    }

    /**
     * Indicate session state/resolution.
     **/
    @Override
    public void onLiveness2SessionResult(Liveness2SessionResultCode liveness2SessionResultCode) {
        if (isVisible()) {
            switch (liveness2SessionResultCode) {
                case Liveness2SessionResultFAIL:
                case Liveness2SessionResultPASS:
                case Liveness2SessionResultLiveness2RequirementsFAIL:
                case Liveness2SessionLivenessDetectionFAIL:
                    mCameraVision.stop();
                    mCameraVision.release();
                    break;
                case Liveness2SessionFaceDetectionFAIL:
                    if (mCameraVision != null) {
                        showToast(getResources().getString(R.string.tix_face_missing));

                        mCameraVision.setTitleText(getResources().getString(R.string.tix_face_missing));
                        mCameraVision.stop();
                        mCameraVision.release();

                        getActivity().getSupportFragmentManager().popBackStack();

                    }
                    break;
                case Liveness2SessionResultTimeoutFAIL:
                    showToast(getResources().getString(R.string.tix_timeout));

                    mCameraVision.stop();
                    break;
                case Liveness2SessionINPROGRESS:
                case Liveness2SessionFaceTrackingFAIL:
                case Liveness2SessionDeviceOrientationAngleFAIL:
                case Liveness2SessionFaceTooFarFAIL:
                case Liveness2SessionFaceTooNearFAIL:
                    break;
                case Liveness2SessionResultERROR:
                    showToast(getString(R.string.tix_error));
                    mCameraVision.stop();
                    getActivity().getSupportFragmentManager().popBackStack();
                    break;
            }
        }
    }

    @Override
    public void onGestureChallenge(GestureType gestureType) {
    }
}
