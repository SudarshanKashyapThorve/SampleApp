package com.au10tix.au10sample.AsyncTasks;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.au10tix.au10sample.model.ImageQualityModel;
import com.au10tix.au10sample.model.Liveness2ResultModel;
import com.au10tix.au10sample.model.LivenessResultModel;
import com.google.gson.Gson;
import com.senticore.au10tix.sdk.algorithms.CropFace;
import com.senticore.au10tix.sdk.algorithms.FindBarcode;
import com.senticore.au10tix.sdk.algorithms.ImageQuality;
import com.senticore.au10tix.sdk.algorithms.Transform;
import com.senticore.au10tix.sdk.cameraVision.detectionResult.BarcodeDetectionResult;
import com.senticore.au10tix.sdk.cameraVision.detectionResult.DetectionResult;
import com.senticore.au10tix.sdk.cameraVision.detectionResult.DocumentDetectionResult;
import com.senticore.au10tix.sdk.cameraVision.detectionResult.FaceDetectionResult;
import com.senticore.au10tix.sdk.cameraVision.detectionResult.Liveness2DetectionResults;
import com.senticore.au10tix.sdk.cameraVision.detectionResult.LivenessDetectionResults;
import com.senticore.au10tix.sdk.enums.Liveness2SessionResultCode;
import com.senticore.au10tix.sdk.image.ImageContainer;
import com.senticore.au10tix.sdk.image.jpeg.JpegImage;
import com.senticore.au10tix.sdk.utils.ImageUtil;

import java.lang.ref.WeakReference;

public class DetectionResultAsyncTask extends AsyncTask<DetectionResult, Void, ResultObject> {
    static final int FACE_DETECTION_RESULT = 0;
    static final int DOCUMENT_DETECTION_RESULT = 1;
    static final int BARCODE_DETECTION_RESULT = 2;
    static final int LIVENESS_DETECTION_RESULT = 3;
    static final int LIVENESS2_DETECTION_RESULT = 4;

    private WeakReference<ProgressBar> aProgressBar;

    public DetectionResultAsyncTask(ProgressBar progressBar) {
        aProgressBar = new WeakReference<>(progressBar);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        aProgressBar.get().post(new Runnable() {
            @Override
            public void run() {
                aProgressBar.get().setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    protected ResultObject doInBackground(DetectionResult... detectionResults) {

        DetectionResult currentResult = detectionResults[0];
        ResultObject resultObject = null;
        switch (currentResult.getType()) {
            case FACE_DETECTION_RESULT: {
                FaceDetectionResult faceDetectionResult = (FaceDetectionResult) currentResult;

                ImageContainer ic = ImageUtil.jpegImageToImageContainer(faceDetectionResult.getJpegImage());
                CropFace cropFace = new CropFace(ic);
                ImageContainer icCropped = cropFace.crop(faceDetectionResult.getQuadrangle());
                ic.release();
                JpegImage croppedFacedJpeg = ImageContainer.getJpegPhoto(icCropped);
                icCropped.release();
                resultObject = new ResultObject(croppedFacedJpeg, FACE_DETECTION_RESULT);
                resultObject.setExtra("");

                break;
            }

            case DOCUMENT_DETECTION_RESULT: {
                DocumentDetectionResult documentDetectionResult = (DocumentDetectionResult) currentResult;
                ImageContainer ic = ImageUtil.jpegImageToImageContainer(documentDetectionResult.getJpegImage());
                ImageQuality.Result qr = (new ImageQuality(ic)).getQuality(ImageQuality.IC_ALL, true);

                ImageQualityModel imageQualityModel = new ImageQualityModel(qr);

                ImageContainer icTransformed = new Transform(ic).transformByRectangle(documentDetectionResult.getQuadrangle()).getResult();
                ic.release();
                JpegImage croppedRectJpeg = ImageContainer.getJpegPhoto(icTransformed);
                icTransformed.release();

                resultObject = new ResultObject(croppedRectJpeg, DOCUMENT_DETECTION_RESULT);
                resultObject.setExtra(new Gson().toJson(imageQualityModel));
                break;
            }
            case BARCODE_DETECTION_RESULT: {
                BarcodeDetectionResult barcodeDetectionResult = (BarcodeDetectionResult) currentResult;
                StringBuilder barcodeBuilder = new StringBuilder();
                FindBarcode.Result[] results = barcodeDetectionResult.getResults();
                for (FindBarcode.Result barcodeResult : results) {
                    barcodeBuilder.append(new String(barcodeResult.getRawdata()));
                }
                resultObject = new ResultObject(null, BARCODE_DETECTION_RESULT);
                resultObject.setExtra(barcodeBuilder.toString());
                break;
            }
            case LIVENESS_DETECTION_RESULT: {
                LivenessDetectionResults livenessDetectionResults = (LivenessDetectionResults) currentResult;
                LivenessResultModel livenessResultModel = new LivenessResultModel(livenessDetectionResults.getLivenessDetectionFinalResult());

                ImageContainer ic = ImageUtil.jpegImageToImageContainer(livenessDetectionResults.getFaceDetectionResult().getJpegImage());
                CropFace cropFace = new CropFace(ic);
                ImageContainer icCropped = cropFace.crop(livenessDetectionResults.getFaceDetectionResult().getQuadrangle());
                ic.release();

                JpegImage croppedFacedJpeg = ImageContainer.getJpegPhoto(icCropped);
                icCropped.release();

                resultObject = new ResultObject(croppedFacedJpeg, LIVENESS_DETECTION_RESULT);
                resultObject.setExtra(new Gson().toJson(livenessResultModel));

                break;
            }
            case LIVENESS2_DETECTION_RESULT: {

                Liveness2DetectionResults liveness2DetectionResults = ((Liveness2DetectionResults) currentResult);

                if (liveness2DetectionResults.getSessionResultCode() == null) {
                    liveness2DetectionResults.setSessionResultCode(Liveness2SessionResultCode.Liveness2SessionResultFAIL);
                }
                if (null != liveness2DetectionResults.getLivenessDetectionResults()) {

                    ImageContainer ic = ImageUtil.jpegImageToImageContainer(liveness2DetectionResults.getLivenessDetectionResults().getFaceDetectionResult().getJpegImage());

                    CropFace cropFace = new CropFace(ic);
                    ImageContainer icCropped = cropFace.crop(liveness2DetectionResults.getLivenessDetectionResults().getFaceDetectionResult().getQuadrangle());
                    ic.release();
                    JpegImage croppedFacedJpeg = ImageContainer.getJpegPhoto(icCropped);
                    icCropped.release();
                    resultObject = new ResultObject(croppedFacedJpeg, LIVENESS2_DETECTION_RESULT);
                    Liveness2ResultModel liveness2ResultModel = new Liveness2ResultModel(liveness2DetectionResults);

                    resultObject.setExtra(new Gson().toJson(liveness2ResultModel));
                }

                break;
            }

        }


        return resultObject;
    }


    @Override
    protected void onPostExecute(ResultObject resultObject) {
        super.onPostExecute(resultObject);
        aProgressBar.get().setVisibility(View.GONE);
        aProgressBar.clear();
    }
}
