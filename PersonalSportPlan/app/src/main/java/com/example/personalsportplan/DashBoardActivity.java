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

public class DashBoardActivity extends AppCompatActivity{

    private long timestamp;
    private TextView textViewStepCounter;
    private TextView textViewAcceleration;
    private Thread detectorTimeStampUpdaterThread;
    private Handler handler;
    private boolean isRunning = true;

//    TextView max_speed;
//    TextView max_acceleration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

//        max_speed = findViewById(R.id.max_speed);
//        max_acceleration = findViewById(R.id.max_acceleration);
//        try {
//            FileInputStream fileIn = openFileInput("mytextfile.txt");
//            InputStreamReader InputRead = new InputStreamReader(fileIn);
//            char[] inputBuffer= new char[100];
//            String s="";
//            int charRead;
//
//            while ((charRead=InputRead.read(inputBuffer))>0) {
//                // char to string conversion
//                String readstring=String.copyValueOf(inputBuffer,0,charRead);
//                s +=readstring;
//            }
//            InputRead.close();
//            String items[] = s.split(" ");
//            max_speed.setText(items[0]);
//            max_acceleration.setText(items[1]);
//        } catch (Exception e) {
//            String s = "N/A";
//            max_speed.setText((CharSequence)s);
//        }
        textViewAcceleration = (TextView) findViewById(R.id.dashb_id2);
        textViewStepCounter = (TextView) findViewById(R.id.dashb_id1);
        registerForSensorEvents();
        setupDetectorTimestampUpdaterThread();

    }
    public void registerForSensorEvents() {
        SensorManager sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Step Counter
        sManager.registerListener(new SensorEventListener() {

                                    @Override
                                    public void onSensorChanged(SensorEvent event) {
                                        float steps = event.values[0];
                                        textViewStepCounter.setText((int) steps + "");
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
                                          }
                                      }
                                      @Override
                                      public void onAccuracyChanged(Sensor sensor, int accuracy) {

                                      }
                                  }, sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
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


    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
        detectorTimeStampUpdaterThread.interrupt();
    }
}
