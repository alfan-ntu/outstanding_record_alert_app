package com.example.azadmin.phpmysql;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.app.NotificationCompat;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText usernameField,passwordField;
    private EditText serverIPField;
    private TextView status,role,method, timerValue;
    private Notification mBuilder;
    private CountDownTimer countDownTimer;
    private NotificationManager notificationManager;

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
        method = (TextView)findViewById(R.id.textView9);
        timerValue = (TextView)findViewById(R.id.textView10);

        addNotification(0);
    }

    private void addNotification(int notificationID){
//
//  Create a Notification object
//

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.app_icon);
//                        .setContentTitle("mydb armed")
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
        method.setText("Get Method");
        new SigninActivity(this,status,role,0).execute(username,password);
        startTimer();
        countDownTimer.start();

        addNotification(1);

    }

    public void loginPost(View view){
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        method.setText("Post Method");
        new SigninActivity(this,status,role,1).execute(username,password);
        startTimer();
        countDownTimer.start();
    }

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
                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();

                Toast toast = Toast.makeText(getApplicationContext(), "Timer is reset", Toast.LENGTH_SHORT);
                toast.show();

                timerValue.setText("Timer done! Reset Timer");
                countDownTimer.start();
                new SigninActivity(MainActivity.this,status,role,0).execute(username,password);

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
