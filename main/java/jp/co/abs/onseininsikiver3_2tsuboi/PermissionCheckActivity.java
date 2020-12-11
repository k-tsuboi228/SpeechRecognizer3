package jp.co.abs.onseininsikiver3_2tsuboi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class PermissionCheckActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_permissions);
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPermissions();
    }

    @SuppressLint("NewApi")
    private void checkPermissions() {
        ArrayList<String> requestPermissions = new ArrayList<>();
        if (23 <= Build.VERSION.SDK_INT) {

            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions.add(Manifest.permission.RECORD_AUDIO);
            }
        }

        if (requestPermissions.isEmpty()) {
            handlePermissionsSuccess();
        } else {
            buildPermissionsRequest(requestPermissions.toArray(new String[0]));
        }
    }

    @SuppressLint("NewApi")
    private void buildPermissionsRequest(String[] permissionsToRequest) {
        requestPermissions(permissionsToRequest, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allSuccess = true;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                Log.e("PTest", String.format(Locale.US, "%s is permission denied.", permissions[i]));
                allSuccess = false;
            }
        }
        if (allSuccess) {
            handlePermissionsSuccess();
        } else {
            finish();
        }
    }

    private void handlePermissionsSuccess() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
