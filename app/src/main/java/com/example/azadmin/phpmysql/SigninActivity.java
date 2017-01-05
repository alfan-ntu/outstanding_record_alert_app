package com.example.azadmin.phpmysql;

import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by lcadmin on 2016/12/23.
 */

public class SigninActivity extends AsyncTask<String, Integer, String> {
    private TextView statusField,roleField;
    private Button manualCheckButton, regularCheckButton, loginButton;
    private Context context;
    private int byGetOrPost = 0;
    private Notification messageBuilder;

// flag 0 means get and 1 means post.(By default it is get.)
    public SigninActivity(Context context, TextView statusField, TextView roleField,
                          Button loginButton, Button manualCheckButton, Button regularCheckButton,
                          int flag) {
        this.context = context;
        this.statusField = statusField;
        this.roleField = roleField;
        this.manualCheckButton = manualCheckButton;
        this.regularCheckButton = regularCheckButton;
        this.loginButton = loginButton;
        byGetOrPost = flag;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
   }

    @Override
    protected String doInBackground(String... arg0) {
 //       messageBuilder = (Notification) MainActivity
        Log.d("SigninActivity debug", "doInBackground is executed");
        if(byGetOrPost == 0){ //means by Get Method
            try{
                String username = (String)arg0[0];
                String password = (String)arg0[1];
                String serverIP = (String)arg0[2];
                String link = "http://"+serverIP+"/login.php?username="+username+"&password="+password;

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
        } else{
            try{
                String username = (String)arg0[0];
                String password = (String)arg0[1];
                String serverIP = (String)arg0[2];

                String link="http://"+serverIP+"/loginpost.php";
                String data  = URLEncoder.encode("username", "UTF-8") + "=" +
                        URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" +
                        URLEncoder.encode(password, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write( data );
                wr.flush();

                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }

                return sb.toString();
            } catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onPostExecute(String result){
        handleSigninResult(result);
    }

    private void handleSigninResult(String result) {
/*
 * reference to string resource in the MainActivity context
 */
        String adminRoleString = this.context.getString(R.string.adminRoleDB);
        String userRoleString = this.context.getString(R.string.userRoleDB);
        String logoutButtonString = this.context.getString(R.string.logoutButtonText);

        if (result.equals(adminRoleString)){
            Log.d("SigninActivity", "My role is an administrator");
            this.statusField.setText(R.string.legalLogin);
            this.roleField.setText(R.string.adminRoleDisplay);
            this.regularCheckButton.setEnabled(true);
            this.manualCheckButton.setEnabled(true);
            this.loginButton.setText(logoutButtonString);
        } else if (result.equals(userRoleString)){
            Log.d("SigninActivity", "I am a regular user");
            this.statusField.setText(R.string.legalLogin);
            this.roleField.setText(R.string.userRoleDisplay);
            this.regularCheckButton.setEnabled(true);
            this.manualCheckButton.setEnabled(true);
            this.loginButton.setText(logoutButtonString);
        } else {
            Log.d("SigninActivity", "I am an intruder");
            this.statusField.setText(R.string.Status);
            this.roleField.setText(R.string.illegalLogin);
        }
    }

}
