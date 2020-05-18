package com.au10tix.au10sample.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.au10tix.au10sample.DetectionTypes;
import com.au10tix.au10sample.MainActivity;
import com.au10tix.au10sample.R;
import com.au10tix.au10sample.model.ChallengeModel;
import com.au10tix.au10sample.model.ImageQualityModel;
import com.au10tix.au10sample.model.Liveness2ResultModel;
import com.au10tix.au10sample.model.LivenessResultModel;
import com.google.gson.Gson;


public class ResultFragment extends Fragment {

    private static final String ARG_DETECTION_TYPE = "resultType";
    private static final String ARG_STRINGED_DATA = "stringExtra";
    private static ResultFragment fragment;

    private String mStringExtra;
    private String mBarcodeData;
    private String mTitleText;

    private int mDetectionResultType;
    private ImageQualityModel imageQualityParams;
    private LivenessResultModel livenessResultParams;
    private Liveness2ResultModel liveness2ResultParams;

    private OnResultFragmentInteractionListener mListener;
    private ImageView croppedResultPhotoImageView;
    private DetectionTypes currentDetectionType;
    private Bitmap photoBitmap;

    public ResultFragment() {
    }

    public static ResultFragment newInstance(int detectionType, String stringExtra) {
        if (fragment == null) {
            fragment = new ResultFragment();
        }

        Bundle args = new Bundle();
        args.putInt(ARG_DETECTION_TYPE, detectionType);
        args.putString(ARG_STRINGED_DATA, stringExtra);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mStringExtra = getArguments().getString(ARG_STRINGED_DATA);
            mDetectionResultType = getArguments().getInt(ARG_DETECTION_TYPE, -99);
        }
        mTitleText = getResources().getStringArray(R.array.detectionOptions)[mDetectionResultType] + " " + getString(R.string.resultsuff);
        Gson gson = new Gson();
        currentDetectionType = DetectionTypes.values()[mDetectionResultType];

        switch (currentDetectionType) {
            case FACE_DETECTION:
                break;
            case DOCUMENT_DETECTION:
                imageQualityParams = gson.fromJson(mStringExtra, ImageQualityModel.class);
                break;
            case BARCODE_DETECTION:
                mBarcodeData = mStringExtra;
                break;
            case LIVENESS_DETECTION:
                livenessResultParams = gson.fromJson(mStringExtra, LivenessResultModel.class);
                break;
            case LIVENESS2_DETECTION:
                liveness2ResultParams = gson.fromJson(mStringExtra, Liveness2ResultModel.class);
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_result, container, false);
        ((TextView) root.findViewById(R.id.resultTitle)).setText(mTitleText);
        container = root.findViewById(R.id.container);

        if (null != imageQualityParams) {
            container.addView(inflateDocumentQualityResult(inflater, container));
        }

        if (null != liveness2ResultParams) {
            container.addView(inflateLiveness2Title(inflater, container));
            livenessResultParams = liveness2ResultParams.getLivenessResult();

            for (int i = 0; i < liveness2ResultParams.getChallenges().size(); i++) {
                ChallengeModel currentChallenge = liveness2ResultParams.getChallenges().get(i);
                container.addView(inflateSingleLiveness2Challenge(
                        inflater,
                        container,
                        currentChallenge.getChallengeType(),
                        currentChallenge.isChallengePassed(),
                        currentChallenge.isFaceTrackingMaintained(),
                        i));

            }
        }

        if (null != livenessResultParams) {
            container.addView(inflateLivenessResult(inflater, container));
        }

        if (currentDetectionType != DetectionTypes.BARCODE_DETECTION) {
            container.addView(inflatePhotoResult(inflater, container));
        }

        if (!TextUtils.isEmpty(mBarcodeData)) {
            container.addView(inflateBarcodeResult(inflater, container));
        }

        return root;
    }

    private View inflatePhotoResult(LayoutInflater inflater, ViewGroup container) {
        View face = inflater.inflate(R.layout.photo_layout, container, false);
        croppedResultPhotoImageView = face.findViewById(R.id.photoImageView);

        return face;
    }

    private View inflateDocumentQualityResult(LayoutInflater inflater, ViewGroup container) {
        View doc = inflater.inflate(R.layout.iq_params_layout, container, false);

        if (imageQualityParams != null) {

            ((TextView) doc.findViewById(R.id.sharpnessDetailTextView)).setText(imageQualityParams.getSharpness() + "");
            ((TextView) doc.findViewById(R.id.brightnessDetailTextView)).setText(imageQualityParams.getBrightness() + "");
            ((TextView) doc.findViewById(R.id.saturationDetailTextView)).setText(imageQualityParams.getSaturation() + "");
            ((TextView) doc.findViewById(R.id.reflectionDetailTextView)).setText(imageQualityParams.getReflection() + "");
            ((TextView) doc.findViewById(R.id.noiseDetailTextView)).setText(imageQualityParams.getNoise() + "");
            ((TextView) doc.findViewById(R.id.contrastDetailTextView)).setText(imageQualityParams.getContrast() + "");
        }

        return doc;
    }

    private View inflateBarcodeResult(LayoutInflater inflater, ViewGroup container) {
        View barcode = inflater.inflate(R.layout.barcode_data_layout, container, false);
        ((TextView) barcode.findViewById(R.id.barcodeData)).setText(mBarcodeData);

        return barcode;
    }

    private View inflateLivenessResult(LayoutInflater inflater, ViewGroup container) {
        View liveness = inflater.inflate(R.layout.liveness_params_layout, container, false);
        ((TextView) liveness.findViewById(R.id.bpDetailTextView)).setText(livenessResultParams.getBinaryPattern() + "");
        ((TextView) liveness.findViewById(R.id.ofDetailTextView)).setText(livenessResultParams.getOpticalFlow() + "");
        ((TextView) liveness.findViewById(R.id.crDetailTextView)).setText(livenessResultParams.getColorRange() + "");
        return liveness;
    }


    private View inflateSingleLiveness2Challenge(LayoutInflater inflater, ViewGroup container, String text, boolean pass, boolean tracked, int order) {
        View challenge = inflater.inflate(R.layout.challenge_item_layout, container, false);
        ((TextView) challenge.findViewById(R.id.challengeOrder)).setText("#" + (order + 1));

        ((TextView) challenge.findViewById(R.id.challengeTitle)).setText(text);

        if (!pass) {
            ((TextView) challenge.findViewById(R.id.challengeTitle)).setTextColor(getResources().getColor(R.color.tix_fail_color));
        }

        if (!tracked) {
            ((TextView) challenge.findViewById(R.id.trackingState)).setVisibility(View.VISIBLE);
        }

        return challenge;
    }

    private View inflateLiveness2Title(LayoutInflater inflater, ViewGroup container) {
        View title = inflater.inflate(R.layout.session_description_layout, container, false);
        ((TextView) title.findViewById(R.id.sessionDecription)).setText(liveness2ResultParams.getSessionResult());

        return title;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentDetectionType != DetectionTypes.BARCODE_DETECTION) {

            photoBitmap = ((MainActivity) getActivity()).getResultObject().getPhotoBitmap();
            if (photoBitmap != null) {
                croppedResultPhotoImageView.setImageBitmap(photoBitmap);

            }

        }
    }


    public void onBackButtonPressed() {
        if (mListener != null) {
            mListener.onBack();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if ((croppedResultPhotoImageView) != null) {
            ((BitmapDrawable) croppedResultPhotoImageView.getDrawable()).getBitmap().recycle();
        }
        mBarcodeData = null;
        imageQualityParams = null;
        livenessResultParams = null;
        liveness2ResultParams = null;
        if (photoBitmap != null) {
            photoBitmap.recycle();
            photoBitmap = null;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        if (context instanceof OnResultFragmentInteractionListener) {
            mListener = (OnResultFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnResultFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnResultFragmentInteractionListener {
        void onBack();
    }
}
