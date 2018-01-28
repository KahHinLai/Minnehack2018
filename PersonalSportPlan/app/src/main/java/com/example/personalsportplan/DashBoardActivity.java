package com.example.personalsportplan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.widget.TextView;
import android.view.View;
import org.w3c.dom.Text;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.content.Intent;
import android.app.PendingIntent;
import android.app.Notification;
import android.app.NotificationChannel;
import android.os.Build;
import android.support.v4.app.TaskStackBuilder;
import android.app.Activity;

public class DashBoardActivity extends AppCompatActivity{

    private long timestamp;
    private TextView textViewStepCounter;
    private TextView textViewAcceleration;
    private TextView textViewRotation;
    private TextView AlertMessage;
    private Thread detectorTimeStampUpdaterThread;
    private Handler handler;
    private boolean isRunning = true;

    float max_speed;
    float max_acceleration;
    float max_flight_climbed;
    float max_heart_rate;
    float max_steps;
    float max_rotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        loadDB();
        textViewRotation = (TextView) findViewById(R.id.dashb_id3);
        textViewAcceleration = (TextView) findViewById(R.id.dashb_id2);
        textViewStepCounter = (TextView) findViewById(R.id.dashb_id1);
        registerForSensorEvents();
        setupDetectorTimestampUpdaterThread();
        AlertMessage = (TextView) findViewById(R.id.alert_message);

    }
    public void registerForSensorEvents() {
        SensorManager sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Step Counter
        sManager.registerListener(new SensorEventListener() {

                                    @Override
                                    public void onSensorChanged(SensorEvent event) {
                                        float steps = event.values[0];
                                        textViewStepCounter.setText((int) steps + "");
                                        if(steps > max_steps) {
                                            AlertMessage.setText("steps exceed max steps");
                                        }
                                    }

                                    @Override
                                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                                    }
                                }, sManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_UI);

        // Step Detector
        sManager.registerListener(new SensorEventListener() {

                                      @Override
                                      public void onSensorChanged(SensorEvent event) {
                                          // Time is in nanoseconds, convert to millis
                                          timestamp = event.timestamp / 1000000;
                                      }

                                      @Override
                                      public void onAccuracyChanged(Sensor sensor, int accuracy) {

                                      }
                                  }, sManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
                SensorManager.SENSOR_DELAY_UI);


        sManager.registerListener(new SensorEventListener() {

                                      @Override
                                      public void onSensorChanged(SensorEvent event) {
                                          // Time is in nanoseconds, convert to millis
                                          Sensor mySensor = event.sensor;

                                          if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                                              float x = event.values[0];
                                              float y = event.values[1];
                                              float z = event.values[2];
                                              double maccel = Math.sqrt(x*x + y*y + z*z);

                                              if (maccel > 12) {
                                                  textViewAcceleration.setText(String.valueOf(maccel-9.8));
                                              }
                                              if (maccel - 9.8 > max_acceleration) {
                                                  AlertMessage.setText("acceleration("+String.valueOf(maccel-9.8)+") exceed max acceleration: " + String.valueOf(max_acceleration));
                                                  sendNotification("your acceleration exceed max_acceleration");
                                              }
                                          }
                                      }
                                      @Override
                                      public void onAccuracyChanged(Sensor sensor, int accuracy) {

                                      }
                                  }, sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);

        sManager.registerListener(new SensorEventListener() {

                                      @Override
                                      public void onSensorChanged(SensorEvent event) {
                                          // Time is in nanoseconds, convert to millis
                                          Sensor mySensor = event.sensor;

                                          if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
                                              float x = event.values[0];
                                              float y = event.values[1];
                                              float z = event.values[2];
                                              double omegaMagnitude = Math.sqrt(x*x + y*y + z*z);

                                              if (omegaMagnitude > 0.5) {
                                                  textViewRotation.setText(String.valueOf(omegaMagnitude));
                                              }
                                              if (omegaMagnitude > max_rotation) {
                                                  AlertMessage.setText("rotation exceed max rotation");
                                              }
                                          }
                                      }
                                      @Override
                                      public void onAccuracyChanged(Sensor sensor, int accuracy) {

                                      }
                                  }, sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_UI);


    }

    private void setupDetectorTimestampUpdaterThread() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };

        detectorTimeStampUpdaterThread = new Thread() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        Thread.sleep(5000);
                        handler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        detectorTimeStampUpdaterThread.start();
    }

    private void loadDB() {
        try {
            FileInputStream fileIn = openFileInput("mytextfile.txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);
            char[] inputBuffer= new char[200];
            String s="";
            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;
            }
            InputRead.close();
            String items[] = s.split(" ");
            float values[] = Stringtofloat(items);
            max_speed = values[0];
            max_acceleration = values[1];
            max_flight_climbed = values[2];
            max_heart_rate = values[3];
            max_steps = values[4];
            max_rotation = values[5];

        } catch (Exception e) {

        }

    }

    private float[] Stringtofloat(String [] stringArray) {
        float[] floatArray = new float[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            floatArray[i] = Float.parseFloat(stringArray[i]);
        }
        return floatArray;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
        detectorTimeStampUpdaterThread.interrupt();
    }

    public void clearalert(View v) {
      AlertMessage.setText("");
    }

    public void sendNotification(String s) {
        Context mContext = this;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext.getApplicationContext(), "notify_001");
        Intent ii = new Intent(mContext.getApplicationContext(), DashBoardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.setBigContentTitle("Alert");
        bigText.setSummaryText("summary");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Your Title");
        mBuilder.setContentText(s);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }
}
