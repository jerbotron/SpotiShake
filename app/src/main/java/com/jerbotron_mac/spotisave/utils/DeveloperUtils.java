package com.jerbotron_mac.spotisave.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class DeveloperUtils {

    public static String getAssetAsString(String assetName, Context context){
        String assetString = null;
        InputStream assetStream;
        try {
            assetStream = context.getAssets().open(assetName);
            if(assetStream != null){
                java.util.Scanner s = new java.util.Scanner(assetStream).useDelimiter("\\A");
                assetString = s.hasNext() ? s.next() : "";
                assetStream.close();
            } else{
                Log.e(DeveloperUtils.class.getName(), "Asset not found:" + assetName);
            }
        } catch (IOException e) {
            Log.e(DeveloperUtils.class.getName(), "Error getting asset as string: " + e.getMessage() );

        }
        return assetString;
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean hasMicrophonePermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    public static String prependHttp(String url) {
        String prefix = "http://";
        if (url.startsWith("http")) {
            return url;
        }
        return prefix.concat(url);
    }
}
