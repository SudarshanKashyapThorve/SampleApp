package com.au10tix.au10sample.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.au10tix.au10sample.BaseFragment;
import com.au10tix.au10sample.R;
import com.senticore.au10tix.sdk.Common;

import java.lang.ref.WeakReference;

import static com.au10tix.au10sample.BaseDetectionFragment.PERMISSIONS_CODE;


public class LobbyFragment extends BaseFragment {

    private static final String ARG_OPTIONS = "options";
    final protected String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    WeakReference<Spinner> demoSelectionSpinner;

    String appVersion;
    String libVersion;
    private String[] sampleOptions;
    private OnLobbyFragmentInteractionListener mListener;

    public LobbyFragment() {
    }


    public static LobbyFragment newInstance(String[] options) {
        LobbyFragment fragment = new LobbyFragment();
        Bundle args = new Bundle();
        args.putStringArray(ARG_OPTIONS, options);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sampleOptions = getArguments().getStringArray(ARG_OPTIONS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lobby, container, false);
        demoSelectionSpinner = new WeakReference<>(root.findViewById(R.id.demoSelectionSpinner));
        Button sampleSelectionButton = root.findViewById(R.id.sampleSelectionButton);
        TextView exampleDescriptionTextView = root.findViewById(R.id.exampleDescriptionTextView);

        TextView versionTextView = root.findViewById(R.id.versionTextView);
        TextView libVersionTextView = root.findViewById(R.id.libVersionTextView);
        versionTextView.setText("app ver: " + appVersion);
        libVersionTextView.setText("aunl ver: " + libVersion);
        ArrayAdapter<String> spinnerDetectionTypeArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, sampleOptions);
        demoSelectionSpinner.get().setAdapter(spinnerDetectionTypeArrayAdapter);
        demoSelectionSpinner.get().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                exampleDescriptionTextView.setText(getResources().getStringArray(R.array.detectionOptionsDescriptions)[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sampleSelectionButton.setOnClickListener(v -> {
            if (requiredPermissionsGranted()) {
                continueToDetection();
            } else {

                requestPermissions(permissions, PERMISSIONS_CODE);
            }
        });
        return root;
    }

    private void continueToDetection() {
        int position = demoSelectionSpinner.get().getSelectedItemPosition();
        onDetectionSelected(position);
    }


    public void onDetectionSelected(int detection) {
        if (mListener != null) {
            mListener.onDetectionSelected(detection);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersion = pInfo.versionName;
            libVersion = Common.getLibVersionName() + "." + Common.getLibVersionCode();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (context instanceof OnLobbyFragmentInteractionListener) {
            mListener = (OnLobbyFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLobbyFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    protected void continueWithPermissions() {
        continueToDetection();
    }


    public interface OnLobbyFragmentInteractionListener {
        void onDetectionSelected(int selection);
    }
}