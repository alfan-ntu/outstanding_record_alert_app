package com.example.azadmin.phpmysql;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

/**
 * Created by lcadmin on 2017/1/2.
 */

public class RegularCheck extends AsyncTask<String, Integer, String> {
    private String serverIP;
    private Context context;
    private EditText serverIPEditText;
    private static final int alertDuration = 5;

    public RegularCheck(Context context, EditText serverIPEditText) {
        this.context = context;
        this.serverIPEditText = serverIPEditText;
        serverIP = serverIPEditText.getText().toString();
    }

    @Override
    protected void onPreExecute() {
        Log.d("RegularCheck debug", "doPreExecute is executed");
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d("RegularCheck debug", "Periodic check by doInBackground");

        String strAlertDuration, link;
        strAlertDuration = String.valueOf(alertDuration);
        try{
            link = "http://192.168.0.164/check_outstanding_application.php?alertduration="+strAlertDuration;
            URL url = new URL(link);
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(link));
            HttpResponse response = client.execute(request);
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";

            while ((line = in.readLine()) != null){
                sb.append(line);
                break;
            }

            in.close();
            return sb.toString();
        } catch (Exception e) {
            return new String ("Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
