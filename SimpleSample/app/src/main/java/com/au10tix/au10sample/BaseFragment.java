package com.au10tix.au10sample;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import static com.au10tix.au10sample.BaseDetectionFragment.PERMISSIONS_CODE;

public abstract class BaseFragment extends Fragment {
    protected boolean shouldStopRequesting = false;

    protected boolean requiredPermissionsGranted() {
        return ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean camGranted = false;
        boolean locGranted = false;
        if (requestCode == PERMISSIONS_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.CAMERA)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        camGranted = true;
                    }
                }

                if (permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        locGranted = true;
                    }
                }
            }
            if (camGranted && locGranted) {
                continueWithPermissions();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)
                        || !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    shouldStopRequesting = true;
                    new AlertDialog.Builder(getContext())
                            .setTitle("Permissions Required")
                            .setMessage("To continue, open the Settings menu and grant the required permissions.")


                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);

                                }
                            })

                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        }
    }

    protected void showToast(String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected abstract void continueWithPermissions();
}
