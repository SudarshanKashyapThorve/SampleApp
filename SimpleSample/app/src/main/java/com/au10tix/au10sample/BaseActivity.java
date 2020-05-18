package com.au10tix.au10sample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.senticore.au10tix.sdk.Common;


abstract class BaseActivity extends FragmentActivity {
    boolean dependenciesAreReady = false;
    String TAG = "BaseActivity";
    static AsyncTask dependAsync;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Calling Common.isReady() loads the SDK, and verifies that the device can perform Liveness2 detection operations.
         * SDK can be loaded asynchronously, prior to any other SDK related resource consuming operations.
         * */

        dependAsync = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                dependenciesAreReady =  Common.isReady(BaseActivity.this);
                Log.d(TAG, "Vision dependencies ready: "+ dependenciesAreReady);
                return null;
            }
        }.execute();

    }

    protected boolean visionDependenciesPrepared(){

        return dependenciesAreReady;
    }

}
