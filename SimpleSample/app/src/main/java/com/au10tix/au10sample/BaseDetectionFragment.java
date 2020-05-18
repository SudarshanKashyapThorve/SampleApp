package com.au10tix.au10sample;

import android.Manifest;
import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;

import com.senticore.au10tix.sdk.cameraVision.Au10tixCameraVision;
import com.senticore.au10tix.sdk.cameraVision.detectionResult.DetectionResult;

public abstract class BaseDetectionFragment extends BaseFragment {
    public static final int PERMISSIONS_CODE = 22;
    public static final int FACE_DETECTION_MODE = 0;
    public static final int DOCUMENT_DETECTION_MODE = 1;
    public static final int BARCODE_DETECTION_MODE = 2;
    public static final int LIVENESS_DETECTION_MODE = 3;
    public static final int LIVENESS2_DETECTION_MODE = 4;
    private static final String TAG = "Base Detection Fragment";
    final protected String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    protected Au10tixCameraVision.OnBackListener tixBackListener;
    protected Au10tixCameraVision.OnProcessingListener tixProcessingListener;
    protected Au10tixCameraVision.OnCaptureListener tixCaptureListener;
    protected FrameLayout cameraVisionContainer;
    protected Au10tixCameraVision mCameraVision;
    private OnDetectorFragmentInteractionListener mListener;


    public BaseDetectionFragment() {
        tixBackListener = new Au10tixCameraVision.OnBackListener() {
            @Override
            public void OnBack() {
                onBackButtonPressed();
            }
        };

        tixCaptureListener = new Au10tixCameraVision.OnCaptureListener() {
            @Override
            public void onCapture(DetectionResult detectionResult) {
                mCameraVision.stop();
                onDetectionResult(detectionResult);
            }
        };

        tixProcessingListener = new Au10tixCameraVision.OnProcessingListener() {
            @Override
            public void onCaptureInit() {
                Log.d(TAG, "Detection process initiated");
            }
        };
    }


    protected void onBackButtonPressed() {
        if (mListener != null) {
            mListener.onBack();
        }
    }

    protected void onDetectionResult(DetectionResult result) {

        if (mListener != null) {
            mListener.onDetectionResult(result);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDetectorFragmentInteractionListener) {
            mListener = (OnDetectorFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDetectorFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }


    @Override
    public void onPause() {
        super.onPause();
        cameraVisionContainer.removeView(mCameraVision);

        if (mCameraVision != null) {
            mCameraVision.stop();
            mCameraVision.release();
            mCameraVision = null;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    protected void continueWithPermissions() {
        if (mCameraVision == null) {
            buildCameraVision();
        }
    }


    protected void init() {

        if (!requiredPermissionsGranted()) {

            if (!shouldStopRequesting) {
                requestPermissions(permissions, PERMISSIONS_CODE);
            }
        } else if (mCameraVision == null) {
            buildCameraVision();
        }

    }

    protected abstract void buildCameraVision();

    public interface OnDetectorFragmentInteractionListener {
        void onDetectionResult(DetectionResult result);

        void onBack();
    }
}
