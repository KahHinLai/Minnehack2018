package com.example.personalsportplan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.InputStreamReader;

public class DashBoardActivity extends AppCompatActivity {

    TextView max_speed;
    TextView max_acceleration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        max_speed = findViewById(R.id.max_speed);
        max_acceleration = findViewById(R.id.max_acceleration);
        try {
            FileInputStream fileIn = openFileInput("mytextfile.txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);
            char[] inputBuffer= new char[100];
            String s="";
            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;
            }
            InputRead.close();
            String items[] = s.split(" ");
            max_speed.setText(items[0]);
            max_acceleration.setText(items[1]);
        } catch (Exception e) {
            String s = "N/A";
            max_speed.setText((CharSequence)s);
        }

    }
}
