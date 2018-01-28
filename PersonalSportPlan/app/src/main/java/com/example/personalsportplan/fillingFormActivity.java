package com.example.personalsportplan;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class fillingFormActivity extends AppCompatActivity {

    EditText max_speed;
    EditText max_acceleration;
    EditText max_flight_climbed;
    EditText max_heart_rate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filling_form);

        max_speed = (EditText)findViewById(R.id.max_speed);
        max_acceleration = (EditText) findViewById(R.id.max_acceleration);
        max_flight_climbed = (EditText) findViewById(R.id.max_flights_climbed);
        max_heart_rate = (EditText) findViewById(R.id.max_heart_rate);
    }

    public void WriteBtn(View v) {
        // add-write text into file
        try {
            FileOutputStream fileout = openFileOutput("mytextfile.txt", Context.MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            String inputs = "";
            inputs += max_speed.getText().toString() + " " + max_acceleration.getText().toString()
                    + " " + max_flight_climbed.getText().toString() + " "
                    + max_heart_rate.getText().toString();
            outputWriter.write(inputs);
            outputWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Intent intent = new Intent(this, DashBoardActivity.class);
        startActivity(intent);
    }

}
