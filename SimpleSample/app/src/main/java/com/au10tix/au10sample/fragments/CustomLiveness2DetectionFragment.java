package com.au10tix.au10sample.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.au10tix.au10sample.BaseDetectionFragment;
import com.au10tix.au10sample.R;
import com.senticore.au10tix.sdk.cameraVision.Au10tixCameraVision;
import com.senticore.au10tix.sdk.cameraVision.CameraVision;
import com.senticore.au10tix.sdk.cameraVision.detectionResult.DetectionResult;
import com.senticore.au10tix.sdk.enums.GestureType;
import com.senticore.au10tix.sdk.enums.Liveness2DetectorResultCode;
import com.senticore.au10tix.sdk.enums.Liveness2SessionResultCode;

public class CustomLiveness2DetectionFragment extends BaseDetectionFragment implements Au10tixCameraVision.Liveness2SessionListener {

    private static final String TAG = "Custom Liveness";
    TextView userInteraction;

    public static CustomLiveness2DetectionFragment newInstance() {
        CustomLiveness2DetectionFragment fragment = new CustomLiveness2DetectionFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_custom_liveness2_detector, container, false);

        cameraVisionContainer = root.findViewById(R.id.cameraVisionContainer);
        userInteraction = root.findViewById(R.id.userInteractionTextView);

        return root;
    }

    private void setLabelText(String message) {

        userInteraction.post(new Runnable() {
            @Override
            public void run() {
                if (userInteraction != null && userInteraction.isAttachedToWindow()) {

                    userInteraction.setText(message);
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        userInteraction = null;
        cameraVisionContainer.removeAllViews();
    }

//    @Override
//    public void onDestroyView() {
//
//        cameraVisionContainer.removeAllViews();
//
//        super.onDestroyView();
//    }

    @SuppressLint("MissingPermission")
    @Override
    protected void buildCameraVision() {

        mCameraVision = (new Au10tixCameraVision.Builder(getActivity()))
                .facingMode(CameraVision.CAMERA_FACING_FRONT)
                .detectionMode(CameraVision.LIVENESS2_DETECTION_MODE)
                .onBack(tixBackListener)
                .onCapture(tixCaptureListener)
                .onLiveness2SessionEvents(this)
                .showLiveFinder(false)
                .build();

        cameraVisionContainer.addView(mCameraVision);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCameraVision != null) {

            mCameraVision.setTitleText("");
        }
    }


    @Override
    public void onChallengeState(Liveness2DetectorResultCode liveness2DetectorResultCode) {
    }


    @Override
    public void onLiveness2SessionResult(Liveness2SessionResultCode liveness2SessionResultCode) {
        if (isVisible()) {
            switch (liveness2SessionResultCode) {
                case Liveness2SessionResultFAIL:
                case Liveness2SessionResultPASS:
                case Liveness2SessionResultLiveness2RequirementsFAIL:
                case Liveness2SessionLivenessDetectionFAIL:

                    if (mCameraVision != null) {
                        mCameraVision.stop();
                    }
                    break;
                case Liveness2SessionFaceDetectionFAIL:
                    if (mCameraVision != null) {
                        mCameraVision.stop();
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.tix_face_missing), Toast.LENGTH_SHORT).show();
                    }
                    getActivity().getSupportFragmentManager().popBackStack();
                    break;
                case Liveness2SessionResultTimeoutFAIL:

                    if (mCameraVision != null) {
                        mCameraVision.stop();
                    }
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.tix_timeout), Toast.LENGTH_SHORT).show();
                    break;
                case Liveness2SessionINPROGRESS:
                    break;
                case Liveness2SessionFaceTrackingFAIL:
                    break;
                case Liveness2SessionDeviceOrientationAngleFAIL:
                    setLabelText(getResources().getString(R.string.tix_device_orientation));
                    break;
                case Liveness2SessionFaceTooFarFAIL:
                    setLabelText(getResources().getString(R.string.tix_face_too_small));
                    break;
                case Liveness2SessionFaceTooNearFAIL:
                    setLabelText(getResources().getString(R.string.tix_face_too_large));
                    break;
                case Liveness2SessionResultERROR:
                    showToast(getString(R.string.tix_error));

                    if (mCameraVision != null) {
                        mCameraVision.stop();
                    }
                    getActivity().getSupportFragmentManager().popBackStack();
                    break;
            }
        }
    }

    @Override
    protected void onDetectionResult(DetectionResult result) {
        setLabelText(getResources().getString(R.string.tix_test_ended));
        mCameraVision.stop();
        super.onDetectionResult(result);
    }

    @Override
    public void onGestureChallenge(GestureType gestureType) {
        switch (gestureType) {

            case GestureTypeFaceForward:
                setLabelText(getResources().getString(R.string.tix_face_forward));
                break;

            case GestureTypePanLeft:
                setLabelText(getResources().getString(R.string.tix_turn_left));
                break;

            case GestureTypePanRight:
                setLabelText(getResources().getString(R.string.tix_turn_right));
                break;

            case GestureTypeSmile:
                setLabelText(getResources().getString(R.string.tix_smile));
                break;

            case GestureTypeEyesClosed:
                setLabelText(getResources().getString(R.string.tix_close_eyes));
                break;
        }
    }
}
