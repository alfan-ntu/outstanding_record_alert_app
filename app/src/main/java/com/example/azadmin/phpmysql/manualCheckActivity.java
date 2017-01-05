package com.example.azadmin.phpmysql;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
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
 * Created by Maoyi on 2017/01/05.
 */

public class manualCheckActivity extends AsyncTask<String, Integer, String> {

    private Context context;
    private TextView outstandingRecordNumber;

    public manualCheckActivity(Context context, TextView outstandingRecordNumber){
        this.context = context;
        this.outstandingRecordNumber = outstandingRecordNumber;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

/*
 *   doInBackground needs two parameters to execute, number of day to expire and host IP
 */
    @Override
    protected String doInBackground(String... arg0) {
        String serverIP = (String)arg0[0];
        String numberOfDayToExpire = (String)arg0[1];

        String link = "http://"+serverIP+"/chk_expire_data.php?alert_duration="+numberOfDayToExpire;

        Log.d("manualCheckActivity", "query sting : "+link);

        try{
            URL url = new URL(link);
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(link));
            HttpResponse response = client.execute(request);
            BufferedReader in = new BufferedReader(new
                    InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line="";

            while ((line = in.readLine()) != null) {
                sb.append(line);
                break;
            }
            in.close();
            return sb.toString();
        } catch(Exception e){
            return new String("Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d("manualCheckActivity", "query result value "+s);
        processQueryData(s);
    }

    private void processQueryData(String s){
        int numberOfOutstandingRecord;

        try{
            numberOfOutstandingRecord = Integer.parseInt(s);
            if (numberOfOutstandingRecord > 0){
                outstandingRecordNumber.setText(String.valueOf(numberOfOutstandingRecord));
            }
        } catch (NumberFormatException e){
            System.out.println("could not parse " + e);
        }
    }
}
