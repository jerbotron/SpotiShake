package com.jerbotron_mac.spotishake.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jerbotron_mac.spotishake.R;

import java.io.IOException;
import java.io.InputStream;

public class AppUtils {

    private Context context;

    public AppUtils(Context context) {
        this.context = context;
    }

    public String getAssetAsString(String assetName){
        String assetString = null;
        InputStream assetStream;
        try {
            assetStream = context.getAssets().open(assetName);
            if(assetStream != null){
                java.util.Scanner s = new java.util.Scanner(assetStream).useDelimiter("\\A");
                assetString = s.hasNext() ? s.next() : "";
                assetStream.close();
            } else{
                Log.e(AppUtils.class.getName(), "Asset not found:" + assetName);
            }
        } catch (IOException e) {
            Log.e(AppUtils.class.getName(), "Error getting asset as string: " + e.getMessage() );

        }
        return assetString;
    }

    public void showToast(String message, int length) {
        Toast.makeText(context, message, length).show();
    }

    public boolean hasMicrophonePermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    public static String prependHttp(String url) {
        String prefix = "http://";
        if (url.startsWith("http")) {
            return url;
        }
        return prefix.concat(url);
    }

    public static boolean isStringEmpty(String s) {
        return s == null || s.isEmpty() || s.equals("");
    }

    public static void showCheckNetworkConnectionDialog(Context context) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        View view = alertDialog.getLayoutInflater().inflate(R.layout.dialog_check_network, null);

        Button delete = (Button) view.findViewById(R.id.confirm_ok);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setView(view);
        alertDialog.show();
    }
}
