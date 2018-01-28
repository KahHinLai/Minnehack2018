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
    int Id;

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
        Id = 0;

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
                                                  sendNotification();
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

    public void sendNotification() {
        // The id of the channel.
        String CHANNEL_ID = "my_channel_01";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ResultActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your app to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ResultActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// mNotificationId is a unique integer your app uses to identify the
// notification. For example, to cancel the notification, you can pass its ID
// number to NotificationManager.cancel().
        mNotificationManager.notify(1, mBuilder.build());
    }
}
