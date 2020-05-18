package com.au10tix.au10sample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ProgressBar;

import com.au10tix.au10sample.AsyncTasks.DetectionResultAsyncTask;
import com.au10tix.au10sample.AsyncTasks.ResultObject;
import com.au10tix.au10sample.fragments.CustomControlsFaceDetectionFragment;
import com.au10tix.au10sample.fragments.CustomDocumentQualityIndicationDetectionFragment;
import com.au10tix.au10sample.fragments.CustomLiveness2DetectionFragment;
import com.au10tix.au10sample.fragments.DetectionFragment;
import com.au10tix.au10sample.fragments.Liveness2DetectionFragment;
import com.au10tix.au10sample.fragments.LobbyFragment;
import com.au10tix.au10sample.fragments.ResultFragment;
import com.senticore.au10tix.sdk.algorithms.ImageQuality;
import com.senticore.au10tix.sdk.cameraVision.detectionResult.BarcodeDetectionResult;
import com.senticore.au10tix.sdk.cameraVision.detectionResult.DetectionResult;
import com.senticore.au10tix.sdk.cameraVision.detectionResult.DocumentDetectionResult;
import com.senticore.au10tix.sdk.cameraVision.detectionResult.Liveness2DetectionResults;
import com.senticore.au10tix.sdk.utils.ServerRequestGenerator;

import java.util.concurrent.ExecutionException;

public class MainActivity extends BaseActivity implements
        LobbyFragment.OnLobbyFragmentInteractionListener,
        BaseDetectionFragment.OnDetectorFragmentInteractionListener,
        ResultFragment.OnResultFragmentInteractionListener {
    private static final String ARG_DETECTION_TYPE = "detectionType";

    private static final String requestTag = "CompoundProcessingJSON";

    ResultObject resultObject;
    Liveness2DetectionFragment liveness2DetectionFragment;
    ResultFragment resultFragment;
    Fragment lobby;

    /**
     * In the provided code sample, gathered DetectionResult instances are held locally to demonstrate the server request generator.
     **/

    private DocumentDetectionResult currentDocDetectionResult;
    private ImageQuality.Result currentDocQuality;
    private BarcodeDetectionResult currentBarcode;
    private Liveness2DetectionResults currentLiveness2DetectionResult;
//    private LivenessDetectionResults currentLiveness;

    private ProgressBar progressBar;
    private String extra = "";

    public ResultObject getResultObject() {
        return resultObject;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            proceedToLobby();
        }
        progressBar = findViewById(R.id.progress_circular);
    }

    private void proceedToLobby() {
        if (lobby == null) {
            lobby = LobbyFragment.newInstance(getResources().getStringArray(R.array.detectionOptions));
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null).replace(R.id.container, lobby, Constants.TAG_FRAGMENT_LOBBY);
        fragmentTransaction.commit();
    }

    private void proceedToResultScreen(int type, String extra) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        resultFragment = ResultFragment.newInstance(type, extra);

        fragmentTransaction.replace(R.id.container, resultFragment, Constants.TAG_FRAGMENT_RESULT);
        fragmentTransaction.commit();
    }

    @Override
    public void onDetectionResult(DetectionResult result) {
        DetectionResultAsyncTask detectionResultAsyncTask = new DetectionResultAsyncTask(progressBar);
        detectionResultAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, result);

        try {
            if (resultObject != null) {
                resultObject = null;
            }
            resultObject = detectionResultAsyncTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DetectionTypes currentType = DetectionTypes.values()[result.getType()];

        extra = "";

        switch (currentType) {
            case FACE_DETECTION: {

                proceedToResultScreen(result.getType(), "");

                break;
            }

            case DOCUMENT_DETECTION: {
                currentDocDetectionResult = ((DocumentDetectionResult) result);

                proceedToResultScreen(result.getType(), resultObject.getExtra());

                String toPrint = generateCompoundProcessingRequestJson();
                Log.w(requestTag, toPrint);

                break;
            }
            case BARCODE_DETECTION: {
                currentBarcode = ((BarcodeDetectionResult) result);

                proceedToResultScreen(result.getType(), resultObject.getExtra());

                String toPrint = generateCompoundProcessingRequestJson();
                Log.w(requestTag, toPrint);

                break;
            }
            case LIVENESS_DETECTION: {

                proceedToResultScreen(result.getType(), resultObject.getExtra());

                break;
            }
            case LIVENESS2_DETECTION: {
                currentLiveness2DetectionResult = ((Liveness2DetectionResults) result);
                if (resultObject == null) {
                    onBackPressed();
                    return;
                }

                String resultExtra = resultObject.getExtra();
                proceedToResultScreen(result.getType(), resultExtra);

                break;
            }
            case DOCUMENT_DETECTION_CUSTOM_QUALITY_INDICATION: {
                proceedToResultScreen(result.getType(), resultObject.getExtra());
                break;

            }
        }
    }


    @Override
    public void onBack() {
        onBackPressed();
    }

    @Override
    public void onDetectionSelected(int selection) {
        if (resultObject != null) {
            resultObject = null;
        }

        extra = "";
        DetectionTypes currentType = DetectionTypes.values()[selection];

/**
 * If the dependencies required for Liveness2 are not ready, conduct a Liveness detection operation instead.
 **/
        if (currentType == DetectionTypes.LIVENESS2_DETECTION && !visionDependenciesPrepared()) {
            selection = DetectionTypes.LIVENESS_DETECTION.ordinal();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        BaseDetectionFragment selectedDetectionType = null;

        switch (currentType) {
            case FACE_DETECTION:
            case DOCUMENT_DETECTION:
            case BARCODE_DETECTION:
            case LIVENESS_DETECTION:

                selectedDetectionType = new DetectionFragment();
                Bundle args = new Bundle();
                args.putInt(ARG_DETECTION_TYPE, selection);
                selectedDetectionType.setArguments(args);
                break;

            case LIVENESS2_DETECTION:
                if (liveness2DetectionFragment == null) {
                    liveness2DetectionFragment = Liveness2DetectionFragment.newInstance();
                }

                selectedDetectionType = liveness2DetectionFragment;
                break;
            case DOCUMENT_DETECTION_CUSTOM_QUALITY_INDICATION:
                selectedDetectionType = CustomDocumentQualityIndicationDetectionFragment.newInstance();
                break;
            case FACE_DETECTION_CUSTOM_CONTROLS:
                selectedDetectionType = CustomControlsFaceDetectionFragment.newInstance();
                break;
            case LIVENESS2_DETECTION_CUSTOM_UI:
                selectedDetectionType = CustomLiveness2DetectionFragment.newInstance();
                break;
        }

        fragmentTransaction.addToBackStack(null).replace(R.id.container, selectedDetectionType, Constants.TAG_FRAGMENT_DETECT);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().findFragmentByTag(Constants.TAG_FRAGMENT_LOBBY).isVisible()) {
            finish();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, getSupportFragmentManager().findFragmentByTag(Constants.TAG_FRAGMENT_LOBBY)).commit();
        }
    }


    /**
     * Once all required detection results are gathered and ready to send, pass them to the ServerRequestGenerator to generate the JSON string.
     **/
    public String generateCompoundProcessingRequestJson() {
        String returnedJsonString;

        ServerRequestGenerator compoundProcessingRequestBuilder =
                new ServerRequestGenerator(currentDocDetectionResult)
                        .withFrontSideQualityResult(currentDocQuality)
                        .withDocumentBackSide(currentDocDetectionResult)
                        .withBackSideQualityResult(currentDocQuality)
//                        .setRequestForDataExtractionOnly(true)
                        .withOptionalRequestTag("Optional Request Tag")
                        .withBarcode(currentBarcode)
                        .withPoaDocument(true)
                        .withOptionalPoaTag("Optional POA Tag")
                        .withLiveness2(currentLiveness2DetectionResult)
//                        .withLiveness(currentLiveness) //Provided for backward compatibility purposes.
                ;
        returnedJsonString = compoundProcessingRequestBuilder.build();

        return returnedJsonString;
    }
}
