package com.jerbotron_mac.spotisave.runnables;

import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class LoadAndDisplayTextRunnable implements Runnable{
    String urlStr;
    TextView textView;

    public LoadAndDisplayTextRunnable(String url, TextView textView){
        urlStr = url;
        this.textView = textView;
    }

    @Override
    public void run(){
        try {
            URL url = new URL("http://"+ urlStr);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String tempStr;
            StringBuffer strBuf = new StringBuffer();
            while ((tempStr = in.readLine()) != null) {
                strBuf.append(tempStr);
            }
            in.close();
            strBuf.append("\n");
            textView.setText(strBuf.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}