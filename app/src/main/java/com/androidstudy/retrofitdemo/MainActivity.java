package com.androidstudy.retrofitdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InjectUtils.injectEvent(this);
    }

    @OnClick({R.id.image_one,R.id.image_two})
    public void click(View view){

        switch (view.getId()){
            case R.id.image_one:
                Log.d(TAG, "click: image one");
                break;
            case R.id.image_two:
                Log.d(TAG, "click: image two");
                break;
        }
    }
}