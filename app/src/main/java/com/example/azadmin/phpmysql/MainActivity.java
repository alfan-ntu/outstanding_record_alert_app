package com.example.azadmin.phpmysql;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.app.NotificationCompat;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {
    private EditText usernameField,passwordField;
    private EditText serverIPField;
    private TextView status,role,timerValue,regularCheckValue;
    private Button manualCheckButton, regularCheckButton;
    private Notification mBuilder;
    private CountDownTimer countDownTimer;
    private NotificationManager notificationManager;
    private StringBuffer sb = new StringBuffer("");

    public Notification getMessageBuilder(){
        return mBuilder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameField = (EditText)findViewById(R.id.editText1);
        passwordField = (EditText)findViewById(R.id.editText2);
        serverIPField = (EditText)findViewById(R.id.editTextServerIP);

        status = (TextView)findViewById(R.id.textView6);
        role = (TextView)findViewById(R.id.textView7);
        regularCheckValue = (TextView)findViewById(R.id.textViewRegularCheckValue);
        regularCheckValue.setText("10 sec");
        timerValue = (TextView)findViewById(R.id.textView10);

        manualCheckButton = (Button)findViewById(R.id.buttonManualCheck);
        regularCheckButton = (Button)findViewById(R.id.buttonRegularCheck);

        addNotification(0);
    }

    private void addNotification(int notificationID){
//
//  Create a Notification object
//

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.app_icon);
//                        .setContentTitle("mydb armed")
//                        .setContentText("Info : periodic check is activated!");
//                        .setContentText("Info : periodic check is activated!");

        if (notificationID == 0) {
            mBuilder.setContentTitle("mydb check armed");
            mBuilder.setContentText("Info : periodic check is activated!");
        } else if(notificationID == 1) {
            mBuilder.setContentTitle("mydb check alarm");
            mBuilder.setContentText("Alert : check your customs tax refund record!");
        }
//
// Set the notification receiver class using the intent directing to ViewRecordActivity.class
//
        Intent notificationIntent = new Intent(this, ViewRecordActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack
        stackBuilder.addParentStack(ViewRecordActivity.class);
// Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(notificationIntent);
// Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
//
// Add as notification
//
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify("PHPMySQL app is running", 0, mBuilder.build());
    }


    public void login(View view){
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        String serverIP = serverIPField.getText().toString();

 /*
 * try to hide the virtual keyboard
 */
        View buttonView = this.getCurrentFocus();
        if (buttonView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(buttonView.getWindowToken(), 0);
        }

        new SigninActivity(this,status,role,manualCheckButton,regularCheckButton,0).execute(username,password, serverIP);

        addNotification(1);

    }

/*
 * manualCheck to query database for outstanding application data manually
 */
    public void manualCheck(View view) {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        String serverIP = serverIPField.getText().toString();

 /*
 * try to hide the virtual keyboard
 */
        View buttonView = this.getCurrentFocus();
        if (buttonView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(buttonView.getWindowToken(), 0);
        }

        new SigninActivity(this, status, role, manualCheckButton, regularCheckButton, 1).execute(username, password, serverIP);

    }

    public void regularCheck(View view){
        Log.d("regularCheckButton", "Button clicked");

        startTimer();
        countDownTimer.start();
    }
/*
    An example of a CountDownTimer
 */
    private void startTimer(){
        Log.d ("startTimer debug", "startTimer is invoked");
        timerValue.setText("3");
        countDownTimer = new CountDownTimer(10*1000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                timerValue.setText(""+millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                final int alertDuration = 5;

                Toast toast = Toast.makeText(getApplicationContext(), "Timer is reset", Toast.LENGTH_SHORT);
                toast.show();

                String strAlertDuration, link;
                strAlertDuration = String.valueOf(alertDuration);
                try{
                    Log.d("RegularCheck debug", "Periodic check in onFinish of a CountDown Timer");
                    link = "http://192.168.0.164/check_outstanding_application.php?alertduration="+strAlertDuration;
                    URL url = new URL(link);
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    request.setURI(new URI(link));
                    HttpResponse response = client.execute(request);
                    BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String line = "";

                    while ((line = in.readLine()) != null){
                        sb.append(line);
                        break;
                    }

                    in.close();
//                    return sb.toString();

                } catch (Exception e) {
//                    return new String ("Exception: " + e.getMessage());
                    sb.append("Exception"+e.getMessage());

                }

                timerValue.setText("Timer done! Reset Timer");
                countDownTimer.start();


//                new RegularCheck(MainActivity.this, serverIPField).execute();

            }
        };
    }

    private void cancelTimer(){
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

}
