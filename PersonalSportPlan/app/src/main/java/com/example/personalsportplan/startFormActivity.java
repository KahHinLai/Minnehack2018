package com.example.personalsportplan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class startFormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_form);
    }

    public void fillingForm(View view) {
        // do something in continue button
        Intent intent = new Intent(this, fillingFormActivity.class);
        startActivity(intent);
    }
}
