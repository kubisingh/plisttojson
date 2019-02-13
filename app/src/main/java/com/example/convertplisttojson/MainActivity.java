package com.example.convertplisttojson;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn=(Button)findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ParseFileHelper(MainActivity.this,lister).startParsing();
            }
        });
    }

    OnParseCompleteLister lister = new OnParseCompleteLister() {
        @Override
        public void onComplete(boolean success, Object data) {

        }
    };
}
